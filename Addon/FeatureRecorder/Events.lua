--- @module Events.lua module for the FeatureRecorder AddOn
--- defines tracked events and links them to the type of interaction
--- defines functions for retrieving additional information for each interaction type
--- @author Jennifer Hanna

--- @table for functions to redirect to Blizzard API
FRT_ObjectRetrievalCalls = {
    getZoneText = GetZoneText,
    getSubZoneText = GetSubZoneText,
    getUnitName =  UnitName,
    getQuestName = function()
                    local questID = GetQuestID()
                    local questTitle = C_QuestLog.GetTitleForQuestID(questID)
                    return "Quest="..questTitle
                end,
    getTaxiMapName = function()
                        local taxiMapID = GetTaxiMapID()
                        local mapInfo = C_Map.GetMapInfo(taxiMapID)
                        return "Map="..mapInfo.name
                    end,
    getEventArgument = "getEventArgument",
    getLootItemNames = function ()
                    local objects = {}
                    local loot = GetLootInfo()
                    for slot = 1, GetNumLootItems() do
                        if LootSlotHasItem(slot) then
                            objects[#objects + 1] = loot[slot].item
                        end
                    end
                    return objects
                end,
    getGroupMembers = function ()
                        local members = {}
                        -- raid group and normal group have to be treated differently
                        if IsInRaid() then
                            if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Group feature: Player is in raid") end
                            local prefix = "raid"
                            for i=1,40,1 do
                                local unitID = prefix..i
                                if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Group feature: Raid unit %d name=%s", i, GetUnitName(unitID)) end
                                members[#members +1] = GetUnitName(unitID)
                            end
                        elseif IsInGroup() then
                            if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Group Feature: Player is in group") end
                            local prefix = "party"
                            for i=1,4,1 do
                                local unitID = prefix..i
                                if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Group feature: Group unit %d name=%s", i, GetUnitName(unitID)) end
                                members[#members +1] = GetUnitName(unitID)
                            end
                        end
                        return members
                    end
}

--- @class FRT_Event
FRT_Event = {   type,
                description,
                objectCalls = {}
            }
--- constructor
function FRT_Event:new(iaType, descr, objectRetrievalCalls)
    local self = setmetatable({}, FRT_Event)
    self.__index = self
    self.type = iaType
    self.description = descr
    self.objectCalls = objectRetrievalCalls
    return self
end

FRT_Event.__index = FRT_Event
--- @end class FRT_Event

--- enum-like table of events with their type, description and  (optional) object retrieval functions
--- can be extended by adding further events to the list
FRT_EventEnum = {
    --- Movements:
    -- objects are player character and zone, collected by Blizzard API functions
    ["PLAYER_STARTED_MOVING"] = FRT_Event:new(
            "MOV_1",
            "started moving",
            {
                { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                { FRT_ObjectRetrievalCalls.getZoneText}
            }) ,
    ["PLAYER_STOPPED_MOVING"] = FRT_Event:new(
            "MOV_2",
            "stopped moving",
            {
                { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                { FRT_ObjectRetrievalCalls.getZoneText}
            }),

    --- Zone change:
    -- objects are player character and zone, collected by Blizzard API functions
    ["ZONE_CHANGED_NEW_AREA"] = FRT_Event:new(
            "ZONE_1",
            "entered new area" ,
            {
                { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                { FRT_ObjectRetrievalCalls.getZoneText}
            }),
    ["ZONE_CHANGED"] = FRT_Event:new(
            "ZONE_2",
            "entered new subarea",
            {
                { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                { FRT_ObjectRetrievalCalls.getSubZoneText}
            }),
    ["ZONE_CHANGED_INDOORS"] = FRT_Event:new(
                "ZONE_3",
                "entered indoor area",
            {
                { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                { FRT_ObjectRetrievalCalls.getSubZoneText}
            }),

    --- Object / NPC interactions:
    ["LOOT_OPENED"] = FRT_Event:new(
            "OBJ_1",
            "looting", -- LOOT_READY fires twice, use LOOT_OPENED instead
            { { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                { FRT_ObjectRetrievalCalls.getUnitName, "target"},
                { FRT_ObjectRetrievalCalls.getZoneText},
                {FRT_ObjectRetrievalCalls.getLootItemNames }
            }),
    ["MAIL_SHOW"] = FRT_Event:new(
            "OBJ_2",
            "opened mailbox",
            {
                { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                { FRT_ObjectRetrievalCalls.getZoneText}
            }), -- mailbox: add mail headers/senders in the future?
    ["QUEST_DETAIL"] = FRT_Event:new(
            "NPC_1",
            "quest npc",
            {
                { FRT_ObjectRetrievalCalls.getUnitName, "npc" },
                { FRT_ObjectRetrievalCalls.getZoneText },
                { FRT_ObjectRetrievalCalls.getQuestName }
            }),
    ["TAXIMAP_OPENED"] = FRT_Event:new(
            "NPC_2",
            "flight master",
            {
                { FRT_ObjectRetrievalCalls.getUnitName, "npc" },
                { FRT_ObjectRetrievalCalls.getTaxiMapName }
            }),

    --- Player character interactions
    ["CHAT_MSG_TEXT_EMOTE"] = FRT_Event:new(
            "COMM_1",
            "emote",
            {
                { FRT_ObjectRetrievalCalls.getUnitName, "target" }, -- target = recipient
                { FRT_ObjectRetrievalCalls.getEventArgument, 1 }, -- arg1: message
                { FRT_ObjectRetrievalCalls.getEventArgument, 2 }, -- arg2: sender
            }),
    -- whisper sent is no longer triggering CHAT_MSG_WHISPER, so we have to use a separate event
    ["CHAT_MSG_WHISPER_INFORM"] = FRT_Event:new(
            "COMM_2",
            "whisper sent",
            {
                { FRT_ObjectRetrievalCalls.getEventArgument, 1 }, -- arg1: message
                { FRT_ObjectRetrievalCalls.getEventArgument, 2 }, -- arg2: recipient
            }),
    -- whisper received
    ["CHAT_MSG_WHISPER"] = FRT_Event:new(
            "COMM_3",
            "whisper received",
            {
                { FRT_ObjectRetrievalCalls.getEventArgument, 1 }, -- arg1: message
                { FRT_ObjectRetrievalCalls.getEventArgument, 2 }, -- arg2: sender
            }),
    ["CHAT_MSG_PARTY"] = FRT_Event:new(
            "COMM_4",
            "party chat",
            {
                { FRT_ObjectRetrievalCalls.getEventArgument, 1 }, -- agr1: message
                { FRT_ObjectRetrievalCalls.getEventArgument, 2 }, -- arg2: sender
            }),
    ["CHAT_MSG_PARTY_LEADER"] = FRT_Event:new(
            "COMM_4",
            "party chat",
            {
                { FRT_ObjectRetrievalCalls.getEventArgument, 1 }, -- agr1: message
                { FRT_ObjectRetrievalCalls.getEventArgument, 2 }, -- arg2: sender
            }),
    ["GROUP_ROSTER_UPDATE"] = FRT_Event:new(
            "GRP_1",
            "grouping: group composition update",
            {
                {FRT_ObjectRetrievalCalls.getGroupMembers}
            } ),

    --- Spellcast: can have all sorts of targets
    ["UNIT_SPELLCAST_SENT"] = FRT_Event:new(
            "SPELL_1",
            "sent spellcast",
            {
                { FRT_ObjectRetrievalCalls.getUnitName, "player"}, -- sender is always the player
                { FRT_ObjectRetrievalCalls.getEventArgument, 2 }, -- arg2: target of the spell
                { FRT_ObjectRetrievalCalls.getEventArgument, 4 } -- arg5: spellID
            }),
}