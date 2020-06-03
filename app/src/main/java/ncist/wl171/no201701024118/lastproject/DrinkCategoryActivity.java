package ncist.wl171.no201701024118.lastproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class DrinkCategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_category);

    //指定适配器 适配drinks数组
        ArrayAdapter<Drink> listAdaper = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,Drink.drinks);
        ListView listDrinks = (ListView)findViewById(R.id.list_drinks);
        listDrinks.setAdapter(listAdaper);
        //指定监听器 响应选项单击
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DrinkCategoryActivity.this,DrinkActivity.class);
                intent.putExtra(DrinkActivity.EXTRA_DRINRKID,(int)id);
                startActivity(intent);
            }
        };
        listDrinks.setOnItemClickListener(itemClickListener);
    }
}
