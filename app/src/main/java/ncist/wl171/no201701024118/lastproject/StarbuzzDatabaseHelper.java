package ncist.wl171.no201701024118.lastproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StarbuzzDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="strbuzz_db";
    private static final int DB_VER=1;

    public StarbuzzDatabaseHelper(Context context){
        super(context,DB_NAME,null,DB_VER);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //建表
        db.execSQL("CREATE TABLE DRINK(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
        +"NAME TEXT,"
        +"DESCRIPTION TEXT,"
        +"IMAGE_RESOURCE_ID INTEGER);");
        insertDrink(db,"Latte","Eepresso with steamed milk",R.drawable.tlatte1);
        insertDrink(db,"Cappuccino","Espresso,hot milk,steamed milk foam",R.drawable.cappucion);
                insertDrink(db,"filter","beans & breawed fresh",R.drawable.filter1);

    }

    //定义工具方法 插入数据 参数与数据表的列对应
    private static void insertDrink(SQLiteDatabase db, String name, String description, int resourceId){
        ContentValues drinkValues = new ContentValues();
        drinkValues.put("NAME",name);
        drinkValues.put("DESCRIPTION",description);
        drinkValues.put("IMAGE_RESOURCE_ID",resourceId);
        long result = db.insert("DRINK",null,drinkValues);
        //日志插入结果
        Log.d("sqlite","insert"+name+"_id:"+result);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion<=1){
            db.execSQL("ALTER TABLE DRINK ADD COLUMN FAVOURITE NUMERIC");
        }
        if(oldVersion<=2){
            ContentValues latteDesc = new ContentValues();
            latteDesc.put("Description","Tasty");
            db.update("Drink",latteDesc,
                    "NAME=?",new String[]{"Latte"});
        }
    }
}
