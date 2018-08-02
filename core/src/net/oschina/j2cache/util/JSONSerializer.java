/**
 * Copyright (c) 2015-2017, Winter Lau (javayou@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oschina.j2cache.util;

import org.nustaq.serialization.FSTConfiguration;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 为了实现跨语言的支持，有必要支持 JSON 的序列化
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class JSONSerializer implements Serializer {

    private static final FSTConfiguration conf = FSTConfiguration.createJsonConfiguration();

    @Override
    public String name() {
        return "json";
    }

    @Override
    public byte[] serialize(Object obj) {
        return conf.asByteArray(obj);
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return conf.asObject(bytes);
    }


    public static void main(String[] args) {

        JSONSerializer serializer = new JSONSerializer();

        Person person = new Person();
        person.setName("Winter Lau");
        person.setAge(19);
        person.setSchoolList(Arrays.asList(new School("西北工业大学"), new School("泉州第五中学"), new School("城东中学"), new School("洛南小学")));
        person.setJobs(new HashMap<String, Integer>(){{
            put("creawor", 3);
            put("moabc", 5);
            put("huateng", 3);
            put("oschina", 8);
        }});

        byte[] bytes = serializer.serialize(person);

        System.out.println(new String(bytes));


        Person p = (Person)serializer.deserialize(bytes);

        System.out.println(p);
    }



}

class Person implements Serializable {

    private String name;
    private int age;
    private List<School> schoolList;
    private HashMap<String, Integer> jobs;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<School> getSchoolList() {
        return schoolList;
    }

    public void setSchoolList(List<School> schoolList) {
        this.schoolList = schoolList;
    }

    public HashMap<String, Integer> getJobs() {
        return jobs;
    }

    public void setJobs(HashMap<String, Integer> jobs) {
        this.jobs = jobs;
    }

    @Override
    public String toString() {
        String str = String.format("NAME:%s,AGE:%d%n", name, age);
        for(School sch : schoolList) {
            str += "\t";
            str += sch;
        }

        for(String key : jobs.keySet())
            str += String.format("\tNAME:%s,YEARS:%d%n", key, jobs.get(key));

        return str;
    }

}

class School implements Serializable {

    private String name;

    public School(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("NAME:%s%n", name);
    }
}
