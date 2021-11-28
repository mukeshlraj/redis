package com.jbdl21.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class PersonController {

    private static final String PERSON_KEY_PREFIX = "person::";
    private static final String PERSON_LIST_KEY = "person_list";
    private static final String PERSON_HASH_KEY_PREFIX = "person_hash::";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/set_value")
    public void setValue(@RequestBody Person person) {
        String key = PERSON_KEY_PREFIX + person.getId();
        redisTemplate.opsForValue().set(key, person);
    }

    @GetMapping("/get_value")
    public Person getValue(@RequestParam("id") int id) {
        String key = PERSON_KEY_PREFIX + id;
        return (Person) redisTemplate.opsForValue().get(key);
    }

    // list

    @PostMapping("/lpush")
    public void lPush(@RequestBody Person person) {
        redisTemplate.opsForList().leftPush(PERSON_LIST_KEY, person);
    }

    @PostMapping("/rpush")
    public void rPush(@RequestBody Person person) {
        redisTemplate.opsForList().rightPush(PERSON_LIST_KEY, person);
    }

    @GetMapping("/lrange")
    public List<Person> lRange(@RequestParam("start") int start, @RequestParam("stop") int stop) {
        List<Object> peopleList = redisTemplate.opsForList().range(PERSON_LIST_KEY, start, stop);
        List<Person> personList = new ArrayList<>();
        for (int i = 0; i < peopleList.size(); i++)
            personList.add((Person) peopleList.get(i));
        return personList;
    }

    // hashes

    @PostMapping("/hmset")
    public void hmset(@RequestBody Person person) {
        Map map = objectMapper.convertValue(person, Map.class);
        redisTemplate.opsForHash().putAll(PERSON_HASH_KEY_PREFIX + person.getId(), map);
    }

    @GetMapping("/hgetall")
    public Person hGetALl(@RequestParam("id") int id) {
        Map map = redisTemplate.opsForHash().entries(PERSON_HASH_KEY_PREFIX + id);
        return objectMapper.convertValue(map, Person.class);
    }
}
