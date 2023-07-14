---
--- @module SessionManagement.lua session management for the FeatureRecorder Addon
--- contains SessionManager (Singleton) and Session (class)
--- supports adding new sessions and adding new features
---
--- @author Jennifer Hanna
---

--- @table data structure for extracted features
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

--- @class FRT_Session instances created by FRT_SessionManager
--- attributes: dateTime        - time of session start
---             characterName   - character with which the session was recorded
---             serverName      - server of the character
---             featureTable    - table of recorded features
--- functions:  new()           - create new instance
---             addFeature()    - adds a feature to the featureTable
FRT_Session = {}
FRT_Session.__index = FRT_Session

--- FRT_Session constructor
function FRT_Session:new()
    local self = setmetatable({}, FRT_Session)
    self.dateTime = currentTime()
    self.startTimeStamp = GetServerTime()
    self.characterName = UnitName("player")
    self.serverName = GetRealmName()
    self.featureTable = {}
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Session: New session created") end
    return self
end

--- FRT_Session method addFeature
--- adds a feature to the session
--- Chain of Responsibility pattern: EventHook -> SessionManager -> activeSession
function FRT_Session:addFeature(self, event, timestamp, ...)
    local payload = ...
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Session: New feature: %s", event) end
    local newFeature = FRT_Feature:new(event, timestamp, payload)
    self.featureTable[#self.featureTable + 1] = newFeature
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Session: Added feature to table") end
end

--- helper function for FRT_Session class
--- creates a humanly readable start time for the session
function currentTime()
    local d = C_DateAndTime.GetCurrentCalendarTime()
    local month = CALENDAR_FULLDATE_MONTH_NAMES[d.month]
    local weekDay = CALENDAR_WEEKDAY_NAMES[d.weekday]
    return format("%02d:%02d, %s, %d %s %d", d.hour, d.minute, weekDay, d.monthDay, month, d.year)
    -- example output: 07:55, Friday, 15 March 2019
end
--- @end class FRT_Session




