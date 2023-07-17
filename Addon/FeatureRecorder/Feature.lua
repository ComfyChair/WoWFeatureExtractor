---
--- @module Feature.lua definitions of the FRT_Feature class
--- @author Jennifer Hanna
---

--- local functions for object retrieval from event arguments
local function objectFromArgument(eventName, eventPayload, argument)
    local result = eventPayload[argument]
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Features: Retrieving object from payload: %s", result) end
    -- special case: spell cast
    -- => mixed object retrieval: additional api call to convert spell id to spell name
    if string.find(eventName, "SPELL") then
        result, _, _, _, _, _, _, _ = GetSpellInfo(result)
    end
    return result
end
--- local functions for object retrieval from function call
local function objectsFromAPI(functionName, argument, objectTable)
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Features: Retrieving object from api call") end
    local result = functionName(argument)
    if type(result) ~= "table" then
        -- single object
        if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Features: Api call returned value: %s", result) end
        objectTable[#objectTable + 1] = result
        if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Features: Retrieved object: %s", result) end
    else
        -- multiple objects
        if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Api call returned a table") end
        for _, v in ipairs(result) do
            if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Api call table object %d: %s", #result, v) end
            objectTable[#objectTable + 1] = v
            if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Features: Retrieved object from table: %s", v) end
        end
    end
end

--- @class FRT_Feature instances created by FRT_Session
--- @field time number time of event
--- @field interactionType string interaction type as event name
--- @field payload string payload of the event
FRT_Feature = {
    time = 123,
    type = "dummyType",
    description = "dummy description",
    objects = {}
}
FRT_Feature.__index = FRT_Feature

--- FRT_Feature constructor with parameters
function FRT_Feature:new(eventName, timestamp, ...)
    local self = setmetatable({}, FRT_Feature)
    local eventPayload = ...
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Features: Creating new feature: %s", eventName) end
    local eventData = FRT_EventEnum[eventName]
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Features: EventEnumType: %s", eventData.type) end
    self.time = timestamp
    self.type = eventData.type
    self.description = eventData.description
    self.objects = {}
    -- take method calls from table to derive objects
    if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Features: Object calls: %d", #eventData.objectCalls) end
    for _, objectCall in ipairs(eventData.objectCalls) do
        local functionName, argument = unpack(objectCall)
        if functionName == "getEventArgument" then -- get object from event payload
            self.objects[#self.objects + 1] = objectFromArgument(eventName, eventPayload, argument)
            if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Features: Added to feature table: %s", self.objects[#self.objects]) end
        else
            objectsFromAPI(functionName, argument, self.objects)
            if DLAPI then DLAPI.DebugLog("FeatureRecordingTool", "Features: Multiple results, new objects table: %s", self.objects) end
        end
    end
    return self
end
--- @end class FRT_Feature


