---
--- @module EventHook.lua Event Hook module for the feature recording tool.
--- supports starting and stopping the recording
---
--- @author Jennifer Hanna



--- @description function receives events from the WoW client and delivers them to the SessionManager
--- @param _ self; not used
--- @param eventName string event name as it is received from the WoW client
--- @param ... string payload, can be zero to multiple arguments depending on the event
function frtEventHandler(_, eventName, ...)
    local payload = { ... }
    if DLAPI then
        if ... then
            DLAPI.DebugLog("FeatureRecordingTool", "Detected event: %s - payload: %s", eventName, ...)
        else
            DLAPI.DebugLog("FeatureRecordingTool", "Detected event: %s - no payload", eventName)
        end
    end
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "EventHook:  Passing on to SessionManager %s", eventName) end
    FRT_SessionManager:addFeature(eventName, GetServerTime(), payload)
end

--- @class EventHook
--- WoW frame object, single object
FRT_EventHook = CreateFrame("Frame")
FRT_EventHook:SetScript("OnEvent", frtEventHandler)

--- @description EventHook function that is called when the recording is started by the user
--- registers events and calls the SessionManager to start a new session
function FRT_EventHook.startRecording()
    -- register events
    for eventName,_ in pairs(FRT_EventEnum) do
        if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Registering event: %s", eventName) end
        FRT_EventHook:RegisterEvent(eventName)
    end
    -- start new session
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Starting new session") end
    FRT_SessionManager:newSession()
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Now recording") end
end

--- @description EventHook function that is called when the recording is stopped by the user
--- unregisters events
function FRT_EventHook.stopRecording()
    for eventName,_ in pairs(FRT_EventEnum) do
        FRT_EventHook:UnregisterEvent(eventName)
    end
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Recording stopped") end
end