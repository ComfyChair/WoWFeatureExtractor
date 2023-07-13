---
--- World of Warcraft Feature Recording tool.
--- for information retrieval purposes
---
--- to be used in combination with a conversion tool
--- to allow import of features into the Generic Multimedia Analysis Framework (GMAF)
--- by Stefan Wagenpfeil, http://www.stefan-wagenpfeil.de/gmaf/
---
--- @author Jennifer Hanna

FRT_Addon = LibStub("AceAddon-3.0"):NewAddon("FeatureRecordingTool")
local ldb = LibStub:GetLibrary("LibDataBroker-1.1")
local libDBicon = LibStub("LibDBIcon-1.0", true)

--- Minimap button icon locations
local stopButton = "Interface\\AddOns\\FeatureRecordingTool\\icons\\miniButtonRecording.tga"
local recordButton = "Interface\\AddOns\\FeatureRecordingTool\\icons\\miniButtonStopped.tga"
--- Recording state variable
local recording = false
--- Minimap button SavedVariables table
-- for the libDBicon library
FRT_BtnData = {}

--- Minimap button creation
-- create data object using LibDataBroker library
local miniButton =  ldb:NewDataObject(
        "FRT_Addon", {
            type = "launcher",
            text = "FRT",
            icon = recordButton,
            OnClick = function()
                local buttonName = GetMouseButtonClicked()
                if buttonName == "LeftButton" then
                    if recording then
                        FRT_Addon:stopRecording()
                    else
                        FRT_Addon:startRecording()
                    end
                elseif buttonName == "RightButton" then
                    -- TODO: maybe change this to modifier + rightClick -> less prone to accidental use
                    FRT_Addon:clearRecord()
                end
            end,
            OnTooltipShow = function(tooltip)
                if not tooltip or not tooltip.AddLine then return end
                tooltip:AddLine("Feature Recording Tool")
                tooltip:AddLine("left click starts / stops recording")
                tooltip:AddLine("right click clears memory")
                tooltip:AddLine("-------------------")
                tooltip:AddLine("sessions recorded: "..FRT_sessionCount())
            end,
        })

--- Recording functions
function FRT_Addon:startRecording()
    print("FRT: Recording events...") -- user feedback in in-game console
    FRT_EventHook:startRecording()
    recording = true;
    miniButton.icon = stopButton
    print("Should have changed icon to ", recordButton)
end

function FRT_Addon:stopRecording()
    FRT_EventHook:stopRecording()
    recording = false;
    miniButton.icon = recordButton
    print("FRT: Stopped recording events.") -- user feedback in in-game console
end

function FRT_Addon:clearRecord()
    FRT_Addon:stopRecording()
    FRT_FeatureRecordings = {}
    print("FRT: Cleared recorded events.") -- user feedback in in-game console
end

--- Console commands
--- @field SLASH_FRT_FEATURE_RECORDING1 string slash command for WoW
--- @table SlashCmdList  the internal Blizzard table for slash commands
--- @field SlashCmdList["FRT_FEATURE_RECORDING"] adding an entry to the internal Blizzard table,
---         which makes the slash command + argument available in the WoW user interface
SLASH_FRT_FEATURE_RECORDING1 = "/frt"
SlashCmdList["FRT_FEATURE_RECORDING"] = function(msg)
    if msg == "start" then
        FRT_Addon.startRecording()
    elseif msg == "stop" then
        FRT_Addon.stopRecording()
    elseif msg == "clear" then
        FRT_Addon.clearRecord()
    else
        print("Unknown option for feature recording tool: ", msg)
    end
end

--- this is called after SavedVaraiables were loaded
function FRT_Addon:OnInitialize()
    --- Button creation handled by libDBicon
    -- this must be delayed until SavedVariables were loaded,
    -- therefore, we use the AceAddon library's OnInitialize function
    recording = false
    libDBicon:Register("FRT_Addon", miniButton, FRT_BtnData)
end





