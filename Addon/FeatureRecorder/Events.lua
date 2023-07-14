---
--- @module Events.lua module for the FeatureRecorder AddOn
--- defines tracked events and links them to the type of interaction
--- defines functions for retrieving additional information for each interaction type
--- @author Jennifer Hanna
---
local testing = false

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

--- replaces Blizzard API calls with mock functions for testing
if testing then
    FRT_ObjectRetrievalCalls.getZoneText = "Test Zone"
    FRT_ObjectRetrievalCalls.getSubZoneText = "Test Subzone"
    FRT_ObjectRetrievalCalls.getUnitName =  "Test Unit "
    FRT_ObjectRetrievalCalls.getQuestName = "Test Quest"
    FRT_ObjectRetrievalCalls.getTaxiMapName = "Test Map Name"
    FRT_ObjectRetrievalCalls.getEventArgument = function() return "Test return from event argument"  end
    FRT_ObjectRetrievalCalls.getLootItemNames = {"Test loot 1", "Test loot 2"}
    FRT_ObjectRetrievalCalls.getGroupMembers = {"Hans", "Egon", "Berta"}
end

--- @table of interaction types and their description
local interactionType = {
    MOV_1 = { name = "MOV_1", descr = "started moving"},
    MOV_2 = {name = "MOV_2", descr = "stopped moving"},
    ZONE_1 = {name = "ZONE_1", descr = "entered new area" },
    ZONE_2 = {name = "ZONE_2", descr = "entered new subarea" },
    ZONE_3 = {name = "ZONE_3", descr = "entered indoor area" },
    OBJ_1 = {name = "OBJ_1", descr = "looting" },
    OBJ_2 = {name = "OBJ_2", descr = "opened mailbox" },
    NPC_1 = {name = "NPC_1", descr = "quest npc" },
    NPC_2 = {name = "NPC_2", descr = "flight master" },
    COMM_1 = {name = "COMM_1", descr = "emote" },
    COMM_2 = {name = "COMM_2", descr = "whisper" },
    COMM_3 = {name = "COMM_3", descr = "party chat" },
    GRP_1 = {name = "GRP_1", descr = "grouping: group composition update" },
    SPELL_1 = {name = "SPELL_1", descr = "sent spellcast" },
    DUM_1 = {name = "DUM_1", descr = "dummy description" },
}
--- @class
FRT_Event = {   type = interactionType.DUM_1.name,
                description = interactionType.DUM_1,
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

--- enum-like table of events with their type, description and  (optional) object retrieval functions
--- can be extended by adding further events to the list
FRT_EventEnum = {
    --- Movements:
    -- objects are player character and zone, collected by Blizzard API functions
    ["PLAYER_STARTED_MOVING"] = FRT_Event:new(interactionType.MOV_1.name, interactionType.MOV_1.descr,
                                 { { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                                   { FRT_ObjectRetrievalCalls.getZoneText} }) ,
    ["PLAYER_STOPPED_MOVING"] = FRT_Event:new(interactionType.MOV_2.name,interactionType.MOV_2.descr,
                                 { { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                                   { FRT_ObjectRetrievalCalls.getZoneText} }),
    --- Zone change:
    -- objects are player character and zone, collected by Blizzard API functions
    ["ZONE_CHANGED_NEW_AREA"] = FRT_Event:new(interactionType.ZONE_1.name,interactionType.ZONE_1.descr,
                                 { { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                                   { FRT_ObjectRetrievalCalls.getZoneText} }),
    ["ZONE_CHANGED"] = FRT_Event:new(interactionType.ZONE_2.name, interactionType.ZONE_2.descr,
                        { { FRT_ObjectRetrievalCalls.getUnitName, "player"}, { FRT_ObjectRetrievalCalls.getSubZoneText} }),
    ["ZONE_CHANGED_INDOORS"] = FRT_Event:new(interactionType.ZONE_3.name, interactionType.ZONE_3.value,
                                { { FRT_ObjectRetrievalCalls.getUnitName, "player"}, { FRT_ObjectRetrievalCalls.getZoneText} }),

    --- Object / NPC interactions:
    -- objects are 1. object/npc name, 2. zone, 3. optional (loot items?, quest id)
    ["LOOT_OPENED"] = FRT_Event:new(interactionType.OBJ_1.name, interactionType.OBJ_1.descr, -- LOOT_READY fires twice, use LOOT_OPENED instead
                      { { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                                { FRT_ObjectRetrievalCalls.getUnitName, "target"},
                                { FRT_ObjectRetrievalCalls.getZoneText},
                                {FRT_ObjectRetrievalCalls.getLootItemNames }}),
    ["MAIL_SHOW"] = FRT_Event:new(interactionType.OBJ_2.name, interactionType.OBJ_2.descr,
                     { { FRT_ObjectRetrievalCalls.getUnitName, "player"},
                       { FRT_ObjectRetrievalCalls.getZoneText} }), -- mailbox: add mail headers/senders in the future?
    ["QUEST_DETAIL"] = FRT_Event:new(interactionType.NPC_1.name, interactionType.NPC_1.descr,
                         { { FRT_ObjectRetrievalCalls.getUnitName, "npc" },
                           { FRT_ObjectRetrievalCalls.getZoneText },
                           { FRT_ObjectRetrievalCalls.getQuestName } }),
    ["TAXIMAP_OPENED"] = FRT_Event:new(interactionType.NPC_2.name, interactionType.NPC_2.descr,
                           { { FRT_ObjectRetrievalCalls.getUnitName, "npc" },
                            { FRT_ObjectRetrievalCalls.getTaxiMapName } }), -- change to flight master id (maybe more significant)?

    --- Player character interactions
    -- most objects in this category get added upon feature creation
    ["CHAT_MSG_TEXT_EMOTE"] = FRT_Event:new(interactionType.COMM_1.name, interactionType.COMM_1.descr,
                                { { FRT_ObjectRetrievalCalls.getUnitName, "target" },
                                  { FRT_ObjectRetrievalCalls.getEventArgument, 1 }, -- arg1: message
                                  { FRT_ObjectRetrievalCalls.getEventArgument, 2 }, -- arg2: sender
                                  { FRT_ObjectRetrievalCalls.getEventArgument, 5 } -- arg5: recipient
                                }),
    ["CHAT_MSG_WHISPER"] = FRT_Event:new(interactionType.COMM_2.name, interactionType.COMM_2.descr,
                             {
                               { FRT_ObjectRetrievalCalls.getEventArgument, 1 }, -- arg1: message
                               { FRT_ObjectRetrievalCalls.getEventArgument, 2 }, -- arg2: sender
                               { FRT_ObjectRetrievalCalls.getEventArgument, 5 } -- arg5: recipient
                             }),
    ["CHAT_MSG_PARTY"] = FRT_Event:new(interactionType.COMM_3.name, interactionType.COMM_3.descr,
            {
                         { FRT_ObjectRetrievalCalls.getEventArgument, 1 }, -- agr1: message
                          { FRT_ObjectRetrievalCalls.getEventArgument, 2 }, -- arg2: sender
                          { FRT_ObjectRetrievalCalls.getEventArgument, 5 } -- arg5: recipient
                          }),
    ["GROUP_ROSTER_UPDATE"] = FRT_Event:new(interactionType.GRP_1.name, interactionType.GRP_1.descr,
                         {{FRT_ObjectRetrievalCalls.getGroupMembers}} ),
    -- spellcast: can have all sorts of targets
    ["UNIT_SPELLCAST_SENT"] = FRT_Event:new(interactionType.SPELL_1.name, interactionType.SPELL_1.descr,
            {{ FRT_ObjectRetrievalCalls.getUnitName, "player"}, -- sender is always the player
                        { FRT_ObjectRetrievalCalls.getEventArgument, 2 }, -- arg2: target of the spell
                        { FRT_ObjectRetrievalCalls.getEventArgument, 4 } -- arg5: spellID
                        })

}

-------------          TESTING AREA         ------------------------
----- Unit testing starts

if testing then
    lu = require('luaunit')
    TestFRT_EventEnum = {} --class
    function TestFRT_EventEnum:testQuestBasics()
        local mockFeature = FRT_Event:new("QUEST_DETAIL", "NPC_1", "interacting with quest npc",{"Theo QuestTest", "Testalia Nord", 1234})
        local expected = { eventName="QUEST_DETAIL", type="NPC_1", description="interacting with quest npc", objects={"Theo QuestTest", "Testalia Nord", "questID=1234"}}
        lu.assertEquals(mockFeature, expected)
    end
    function TestFRT_EventEnum:testGetByNameQuest()
        local mockFeature = FRT_EventEnum:getByName("QUEST_DETAIL")
        local expected = { eventName="QUEST_DETAIL", type="NPC_1", description="interacting with quest npc", objects={"Test npc", "Test Zone", 1234}}
        lu.assertEquals(mockFeature, expected)
    end
    -- class TestFRT_EventEnum
    os.exit(lu.LuaUnit.run())
end