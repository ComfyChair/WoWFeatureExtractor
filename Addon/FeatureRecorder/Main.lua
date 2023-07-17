--- World of Warcraft FeatureRecorder Addon
---
--- to be used in combination with the WoWFeatureExtractor java application
--- to allow import of features into the Generic Multimedia Analysis Framework (GMAF)
--- by Stefan Wagenpfeil: http://www.stefan-wagenpfeil.de/gmaf/
---
--- @module Main.lua interface definitions and functions
---
--- @author Jennifer Hanna

--- registering with the AceAddon library is necessary for proper minimap button function
FRT_Addon = LibStub("AceAddon-3.0"):NewAddon("FeatureRecorder")
local ldb = LibStub:GetLibrary("LibDataBroker-1.1")
local libDBicon = LibStub("LibDBIcon-1.0", true)
--- colors
local green = "ff27a620"
local red = "ffd82e20"
local grey = "ffc5c5c5"
local white = "ffffffff"
--- Minimap button icon locations
local stopButton = "Interface\\AddOns\\FeatureRecorder\\icons\\miniButtonRecording.tga"
local recordButton = "Interface\\AddOns\\FeatureRecorder\\icons\\miniButtonStopped.tga"
--- Recording state variable
local recording = false
--- Minimap button SavedVariables table
-- for the libDBicon library
FRT_BtnData = {}
--- tooltip update function
local function updateTooltip(tooltip)
    tooltip:ClearLines()
    tooltip:AddLine("Feature Recording Tool")
    tooltip:AddLine(WrapTextInColorCode("left-click", grey)
            ..WrapTextInColorCode(" starts / stops recording", white))
    tooltip:AddLine(WrapTextInColorCode("right-click", grey)
            ..WrapTextInColorCode(" clears memory", white))
    tooltip:AddLine("------------------------------------")
    tooltip:AddLine("sessions recorded: "..WrapTextInColorCode(FRT_sessionCount(), red))
    if (recording) then
        tooltip:AddLine(WrapTextInColorCode("   currently recording", red))
    else
        tooltip:AddLine(WrapTextInColorCode("   currently not recording", green))
    end
end

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
                    FRT_Addon:clearRecord()
                end
            end,
            OnTooltipShow = function(tooltip)
                if not tooltip or not tooltip.AddLine then return end
                updateTooltip(tooltip)
            end,
        })

--- Recording function: start
function FRT_Addon:startRecording()
    print("FRT: Recording events...") -- user feedback in in-game console
    FRT_EventHook:startRecording()
    recording = true;
    miniButton.icon = stopButton
    updateTooltip(libDBicon.tooltip)
end

--- Recording function: stop
function FRT_Addon:stopRecording()
    FRT_EventHook:stopRecording()
    recording = false;
    miniButton.icon = recordButton
    updateTooltip(libDBicon.tooltip)
    print("FRT: Stopped recording events.") -- user feedback in in-game console
end

--- Recording function: clear recorded sessions
function FRT_Addon:clearRecord()
    if recording then
        FRT_Addon:stopRecording()
    end
    FRT_FeatureRecordings = {}
    updateTooltip(libDBicon.tooltip)
    print("FRT: Cleared recorded events.") -- user feedback in in-game console
end

--- Console command definitons
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
--- necessary for proper minimap button function
function FRT_Addon:OnInitialize()
    --- Button creation handled by libDBicon
    -- this must be delayed until SavedVariables were loaded,
    -- therefore, we use the AceAddon library's OnInitialize function
    recording = false
    libDBicon:Register("FRT_Addon", miniButton, FRT_BtnData)
end