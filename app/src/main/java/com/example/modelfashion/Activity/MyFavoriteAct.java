package com.example.modelfashion.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.modelfashion.Adapter.ProductAdapter;
import com.example.modelfashion.Interface.ApiRetrofit;
import com.example.modelfashion.Model.response.my_product.MyProduct;
import com.example.modelfashion.R;
import com.example.modelfashion.Utility.Constants;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFavoriteAct extends AppCompatActivity {
    Spinner spnFav;
    LinearLayout llFilterFav;
    TextView tvFilterPrice;
    ImageView imgFilterPrice;
    RecyclerView rvFv;
    String user_id = "";
    ProductAdapter productAdapter;
    ArrayList<MyProduct> arrProduct = new ArrayList<>();
    ArrayList<String> arrType = new ArrayList<>();
    String type = "";
    String value = "ASC";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite);
        spnFav = findViewById(R.id.spn_fill_fav);
        llFilterFav = findViewById(R.id.ll_fill_price_fav);
        tvFilterPrice = findViewById(R.id.tv_fill_price_fav);
        imgFilterPrice = findViewById(R.id.img_fill_price_fav);
        rvFv = findViewById(R.id.rv_fav);
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        SetData();
    }
    private void SetData(){
        ApiRetrofit.apiRetrofit.GetFavProduct(user_id).enqueue(new Callback<ArrayList<MyProduct>>() {
            @Override
            public void onResponse(Call<ArrayList<MyProduct>> call, Response<ArrayList<MyProduct>> response) {
                arrProduct = response.body();
                productAdapter = new ProductAdapter(MyFavoriteAct.this,arrProduct);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(MyFavoriteAct.this,2,RecyclerView.VERTICAL,false);
                rvFv.setLayoutManager(gridLayoutManager);
                rvFv.setAdapter(productAdapter);
                for (int i=0; i<arrProduct.size(); i++){
                    arrType.add(arrProduct.get(i).getType());
                }
                Set<String> filter_type = new LinkedHashSet<>();
                filter_type.addAll(arrType);
                arrType.clear();
                arrType.addAll(filter_type);
                arrType.add("T???t c???");
                ArrayAdapter fillTypeAdapter = new ArrayAdapter(MyFavoriteAct.this,R.layout.support_simple_spinner_dropdown_item,arrType);
                spnFav.setAdapter(fillTypeAdapter);
                type = "T???t c???";
                spnFav.setSelection(arrType.size()-1);
                spnFav.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        type = arrType.get(i);
                        GetFilterItem();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                llFilterFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(value.equalsIgnoreCase("ASC")){
                            tvFilterPrice.setText("Gi?? tr??? gi???m d???n");
                            value = "DESC";
                            imgFilterPrice.setImageResource(R.drawable.ic_sort_decrease);
                            GetFilterItem();
                        }else {
                            tvFilterPrice.setText("Gi?? tr??? t??ng d???n");
                            value = "ASC";
                            imgFilterPrice.setImageResource(R.drawable.ic_sort_increase);
                            GetFilterItem();
                        }
                    }
                });
                productAdapter.onItemClickListener(new ProductAdapter.OnItemClick() {
                    @Override
                    public void imgClick(int position, MyProduct product) {
                        Intent intent = new Intent(MyFavoriteAct.this,NewProductDetailAct.class);
                        intent.putExtra(Constants.KEY_PRODUCT_ID,product.getId());
                        intent.putExtra("user_id",user_id);
                        startActivity(intent);
                    }

                    @Override
                    public void imgAddToFavoriteClick(int position, MyProduct product) {
                        Toast.makeText(MyFavoriteAct.this, "S???n ph???m ??ang theo d??i", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ArrayList<MyProduct>> call, Throwable t) {

            }
        });
    }

    private void GetFilterItem() {
        ApiRetrofit.apiRetrofit.GetFilterFavProduct(user_id,type,value).enqueue(new Callback<ArrayList<MyProduct>>() {
            @Override
            public void onResponse(Call<ArrayList<MyProduct>> call, Response<ArrayList<MyProduct>> response) {
                arrProduct.removeAll(arrProduct);
                arrProduct.addAll(response.body());
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ArrayList<MyProduct>> call, Throwable t) {

            }
        });
    }
}