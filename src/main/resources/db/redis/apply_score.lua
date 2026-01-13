-- KEYS[1]: Redis Key (Crew Rank Key)
-- ARGV[1]: Member Key (Member ID)
-- ARGV[2]: Score to add (weight)
-- ARGV[3]: Current Epoch Second (applyAt)
-- ARGV[4]: Multiplier (10,000,000,000)
-- ARGV[5]: Base Epoch Time (BASE_EPOCH_TIME)

local current_weight = tonumber(redis.call('ZSCORE', KEYS[1], ARGV[1]) or 0)
local multiplier = tonumber(ARGV[4])
local base_epoch = tonumber(ARGV[5])

local current_pure_score = math.floor((current_weight + 0.5) / multiplier)
local next_pure_score = current_pure_score + tonumber(ARGV[2])

local relative_epoch = tonumber(ARGV[3]) - base_epoch
local next_weight = (next_pure_score * multiplier) + relative_epoch

redis.call('ZADD', KEYS[1], next_weight, ARGV[1])

return math.floor(next_weight)
