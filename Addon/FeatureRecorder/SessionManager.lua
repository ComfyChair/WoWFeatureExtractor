---
--- @module SessionManager.lua session management for the FeatureRecorder Addon
--- contains SessionManager (Singleton)
--- supports adding new sessions to the session table (FRT_FeatureRecordings),
--- which gets saved to the SavedVariable file
---
--- @author Jennifer Hanna
---

--- @table data structure for recorded sessions
--- initialized as empty table, gets overwritten upon loading of SavedVariables
FRT_FeatureRecordings = {}

--- @function counts the sessions in the SavedVariables file (as #table is not applicable)
function FRT_sessionCount()
    local count = 0
    for _ in pairs(FRT_FeatureRecordings) do
        count = count + 1
    end
    return count
end

--- @class SessionManager Singleton, created by FRT_EventHook
--- @field activeSession FRT_Session currently active session
FRT_SessionManager = {
    activeSession
}
--- FRT_SessionManager constructor
--- creates a new session and adds it to the global table FRT_FeatureRecordings
function FRT_SessionManager:newSession()
    local recordedSessions = FRT_sessionCount()
    local sessionID = "session_" .. (recordedSessions + 1)
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "SessionManager: New session") end
    activeSession = FRT_Session:new()
    FRT_FeatureRecordings[sessionID] = activeSession
end
--- FRT_SessionManager method addFeature
--- adds a feature to the active session
--- Chain of Responsibility pattern: EventHook -> SessionManager -> activeSession
function FRT_SessionManager:addFeature(event, timestamp, ...)
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "SessionManager: New feature: %s", event) end
    local payload = ...
    activeSession:addFeature(activeSession, event, timestamp, payload)
end
--- @end class FRT_SessionManager