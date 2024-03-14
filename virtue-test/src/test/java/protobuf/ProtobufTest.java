package protobuf;

import io.virtue.serialization.protobuf.ProtobufSerializer;
import org.junit.jupiter.api.Test;
import protobuf.p1.PersonProto1;
import protobuf.p2.PersonProto2;

import java.util.ArrayList;

/**
 * @Author WenBo Zhou
 * @Date 2024/2/28 22:25
 */
public class ProtobufTest {

    ProtobufSerializer protobufSerializer = new ProtobufSerializer();

    /**
     * Simple object
     */
    @Test
    public void test1() {
        StudentProto.Student student = StudentProto.Student.newBuilder().setAge(23).setName("zwb").build();
        byte[] bytes = protobufSerializer.serialize(student);
        StudentProto1.Student1 student1 = protobufSerializer.deserialize(bytes, StudentProto1.Student1.class);
        System.out.println(student1);
    }

    /**
     * Nest object
     */
    @Test
    public void test2() {
        PersonProto1.Address1 address1 = PersonProto1.Address1.newBuilder()
                .setCity("suzhou")
                .setZip("000000")
                .setStreet("xietang")
                .build();
        ArrayList<PersonProto1.Contact1> contact1s = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PersonProto1.Contact1 contact1 = PersonProto1.Contact1.newBuilder()
                    .setEmail("wenbochou@163.com-" + i)
                    .setPhone("1528187789-" + i)
                    .build();
            contact1s.add(contact1);
        }
        PersonProto1.Person1 person1 = PersonProto1.Person1.newBuilder()
                .setAge(23)
                .setName("ZWB")
                .setHomeAddress(address1)
                .addAllContacts(contact1s)
                .build();
        byte[] bytes = protobufSerializer.serialize(person1);
        PersonProto2.Person2 deserialize = protobufSerializer.deserialize(bytes, PersonProto2.Person2.class);
        System.out.println(deserialize);
    }
}
