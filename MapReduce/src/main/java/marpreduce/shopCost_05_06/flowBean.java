package shopCost_05_06;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

//序列化形成class
//序列化和反序列化顺序一直

public class flowBean implements Writable {

    private String phone;
    private String add;
    private String name;
    private long consum;


    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(phone);
        out.writeUTF(add);
        out.writeUTF(name);
        out.writeLong(consum);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.phone=in.readUTF();
        this.add=in.readUTF();
        this.name=in.readUTF();
        this.consum=in.readLong();
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public void setConsum(long consum) {
        this.consum = consum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getConsum() {
        return consum;
    }

    public String getAdd() {
        return add;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "flowBean [phobe="+phone+",add="+add+",name="+name+",consum="+consum+"]";
    }
}

