local order = redis.call('incr', KEYS[1])
if order <= tonumber(ARGV[1])
then
    redis.call('HSET', ARGV[2], ARGV[3], order)
    return order
end
return 0