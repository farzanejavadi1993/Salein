package ir.kitgroup.saleinbahraman.Adapters;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.cedarstudios.cedarmapssdk.model.geocoder.forward.ForwardGeocode;

import java.util.List;

import ir.kitgroup.saleinbahraman.R;

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.SearchViewHolder> {

    private List<ForwardGeocode> mItems;

    public void setOnClickItemListener(ClickItem clickRowSearch) {
        this.clickRowSearch = clickRowSearch;
    }


    public interface ClickItem {
        void onRowClick(ForwardGeocode mItem);
    }

    public  ClickItem clickRowSearch;


    static class SearchViewHolder extends RecyclerView.ViewHolder  {

        private TextView mNameTextView;
        private TextView mTypeTextView;
        private TextView mCityTextView;
        private TextView mLocalityTextView;

        private ForwardGeocode mItem;

        SearchViewHolder(View v) {
            super(v);

            mNameTextView = v.findViewById(R.id.search_view_list_item_name);
            mTypeTextView = v.findViewById(R.id.search_view_list_item_type);
            mCityTextView = v.findViewById(R.id.search_view_list_city);
            mLocalityTextView = v.findViewById(R.id.search_view_list_item_locality);




        }

        void bindData(@NonNull ForwardGeocode item) {
            mItem = item;

            if (TextUtils.isEmpty(item.getName())) {
                mNameTextView.setText("");
            } else {
                mNameTextView.setText(item.getName());
            }

            if (TextUtils.isEmpty(item.getPersianType())) {
                mTypeTextView.setText("");
            } else {
                String result = "(" + item.getPersianType() + ") ";
                mTypeTextView.setText(result);
            }

            String city = null;
            if (item.getComponents() != null) {
                city = item.getComponents().getCity();
                if (TextUtils.isEmpty(city)) {
                    mCityTextView.setText("");
                    mCityTextView.setVisibility(View.GONE);
                } else {
                    mCityTextView.setText(city);
                    mCityTextView.setVisibility(View.VISIBLE);
                }
            } else {
                mCityTextView.setText("");
                mCityTextView.setVisibility(View.GONE);
            }

            StringBuilder locality = null;
            if (item.getComponents() != null && item.getComponents().getLocalities() != null) {
                for (int i = 0; i < Math.min(item.getComponents().getLocalities().size(), 3); i++) {
                    String l = item.getComponents().getLocalities().get(i);
                    if (locality == null) {
                        locality = new StringBuilder(l);
                    } else {
                        locality.append("، ").append(l);
                    }
                }
            }
            if (TextUtils.isEmpty(locality != null ? locality.toString() : null)) {
                mLocalityTextView.setText("");
                mLocalityTextView.setVisibility(View.GONE);
            } else {
                mLocalityTextView.setText(locality.toString());
                mLocalityTextView.setVisibility(View.VISIBLE);
                if (mCityTextView.getText().length() != 0) {
                    String result = city != null ? city + "،" : "";
                    mCityTextView.setText(result);
                }
            }
        }

//        @Override
//        public void onClick(View v) {
//         /*   try {
//                LauncherActivity mainActivity = (LauncherActivity) v.getContext();
//                Fragment fragment = mainActivity.getSupportFragmentManager().findFragmentByTag("MapFragment");
//                if (fragment instanceof MapFragment) {
//                    MapFragment fgf = (MapFragment)fragment;
//                    fgf.showItemOnMap(mItem);
//                }
//            }catch (Exception ignore){
//
//            }*/
//
//
//
//        }
    }

    public SearchViewAdapter(List<ForwardGeocode> items) {
        mItems = items;
    }

    @NonNull
    @Override
    public SearchViewAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_autocomplete_item, parent, false);
        return new SearchViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewAdapter.SearchViewHolder holder, int position) {
        ForwardGeocode item = mItems.get(position);
        holder.bindData(item);
        holder.itemView.setOnClickListener(v1 -> clickRowSearch.onRowClick(item));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}