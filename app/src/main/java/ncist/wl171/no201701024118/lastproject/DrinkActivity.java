package ncist.wl171.no201701024118.lastproject;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DrinkActivity extends AppCompatActivity {

    public static final String EXTRA_DRINRKID="drinkId";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);
        int drinkId = getIntent().getExtras().getInt(EXTRA_DRINRKID);
       Drink drink = Drink.drinks[drinkId];
        //显示咖啡名称
        TextView name=(TextView)findViewById(R.id.name);
       name.setText(drink.getName());
        //显示咖啡描述
        TextView description = (TextView)findViewById(R.id.description);
        description.setText(drink.getDescription());
       //显示咖啡图片
       ImageView photo = (ImageView)findViewById(R.id.photo);
        photo.setImageResource(drink.getImageResourceId());
        photo.setContentDescription(drink.getName());
        //实例化 helper对象
        SQLiteOpenHelper starbuzzDatabaseHelper = new StarbuzzDatabaseHelper(this);
//获得数据库引用
        try (SQLiteDatabase db = starbuzzDatabaseHelper.getReadableDatabase()) {
        }catch (SQLException e){
            Log.e("sqlite",e.getMessage());
            Toast toast = Toast.makeText(this,"Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
