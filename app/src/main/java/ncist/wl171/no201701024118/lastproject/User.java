package ncist.wl171.no201701024118.lastproject;

import android.os.Parcel;
import android.os.Parcelable;

/*
       Android组件调用时，Inten携带基本数据类型时直接进行，如果是定义的实体类，则需要序列化实体类
      实体类User，实现接口Parcelable，类似于Java接口Serializable
      头像图片路径为“/images/UAP/”，（UAP=UserAccountPicture）
      头像图片文件名与userID一致，扩展名为.png
*/
public class User implements Parcelable {
    long userID;
    String username;

    public User(long userID, String username) {
        this.userID = userID;
        this.username = username;
    }
    protected User(Parcel in) {
        username = in.readString();
    }
    public long getUserID() {
        return userID;
    }
    public String getUsername() {
        return username;
    }
    @Override
    public String toString() {
        return "userID:" + getUserID() + "," + "username:" + getUsername();
    }
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in.readLong(), in.readString());
        }
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userID);
        dest.writeString(username);
    }
}
