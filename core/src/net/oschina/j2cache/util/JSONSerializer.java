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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.*;

/**
 * 为了实现跨语言的支持，有必要支持 JSON 的序列化
 *
 * 需要支持的数据类型包括：
 *
 * 1. 原生类型
 * 2. 数组类型
 * 3. 集合类型
 * 4. 对象类型
 * 5. 对象数据
 * 6. 对象集合
 *
 * 输出格式：
 *
 * {
 *     "type": "int",
 *     "value": "123"
 * }
 *
 * @author Winter Lau(javayou@gmail.com)
 */
public class JSONSerializer implements Serializer {

    private static final List<Class> primitiveClasses = new ArrayList(){{
        add(Number.class);
        add(Character.class);
        add(CharSequence.class);
    }};

    @Override
    public String name() {
        return "json";
    }

    @Override
    public byte[] serialize(Object obj) {
        JSONObject json = new JSONObject();
        json.put("type", obj.getClass().getName());
        json.put("value", obj);
        String jsonStr = JSON.toJSONString(json);
        return jsonStr.getBytes();
    }

    @Override
    public Object deserialize(byte[] bytes) {
        Object obj = JSON.parse(new String(bytes));
        if(obj instanceof JSONObject){

        }
        return obj;
    }

    public static void main(String[] args) throws Exception {
        JSONSerializer json = new JSONSerializer();
        int[] obj = {10,11,12};
        List<Person> persons = Arrays.asList(new Person("Winter Lau", 19));

        String result = new String(json.serialize(persons));

        System.out.println(result);

    }

    public static class Person {
        private String name;
        private int age;

        public Person(){}

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

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
    }
}
