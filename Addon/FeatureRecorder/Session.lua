---
--- @module Session.lua for the FeatureRecorder Addon
--- contains FRT_Session (class)
--- supports adding new features to a session
---
--- @author Jennifer Hanna
---

--- @class FRT_Session data structure that contains recorded features and general information about the recording
--- instances created by FRT_SessionManager
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