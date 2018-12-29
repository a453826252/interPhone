package interphone.a20160218.com.interphone.datahanding.adapter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import interphone.a20160218.com.interphone.R;

public class ScanResultAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<ScanResult> mScanResult;
    private IOnItemClick<ScanResult> mItemClick;
    public ScanResultAdapter(Context context, List<ScanResult> scanResults,IOnItemClick<ScanResult> itemClick) {
        mContext = context;
        mScanResult = distinct(scanResults);
        mItemClick = itemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,final int viewtype) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.scan_result_list, viewGroup, false);
        return new ViewHold(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ViewHold viewHold = (ViewHold) viewHolder;
        viewHold.bindData(getItem(i));
        viewHold.bindClickEvent(mItemClick,getItem(i));
    }

    @Override
    public int getItemCount() {
        return mScanResult.size();
    }

    private ScanResult getItem(int i) {
        return mScanResult.get(i);
    }

    public List<ScanResult> getData() {
        return mScanResult;
    }
    public void addAll(List<ScanResult> list){
        list = distinct(list);
        if(mScanResult == null){
            mScanResult = new ArrayList<>(list.size());
        }
        mScanResult.addAll(list);
    }

    private List<ScanResult> distinct(List<ScanResult> list) {
        if (list == null || list.size() <= 0) {
            return list;
        }
        List<ScanResult> distinctList = new ArrayList<>();
        for (ScanResult sr : list) {
            boolean exist = false;
            for (ScanResult r : distinctList) {
                if (r.SSID.equals(sr.SSID) && r.capabilities.equals(sr.capabilities)) {
                    exist = true;
                    break;
                }
            }
            if(!exist){
                distinctList.add(sr);
            }
        }
        return distinctList;
    }

    class ViewHold extends RecyclerView.ViewHolder {

        private View itemView;

        private TextView ssid;

        private TextView db;

        private ViewHold(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            ssid = itemView.findViewById(R.id.ssid);
            db = itemView.findViewById(R.id.db);
        }

        private void bindData(ScanResult scanResult) {
            ssid.setText(scanResult.SSID);
            db.setText(String.valueOf(scanResult.level));
        }

        private void bindClickEvent(final IOnItemClick<ScanResult> iOnItemClick,final ScanResult scanResult){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iOnItemClick.onClick(scanResult);
                }
            });
        }

    }
    public interface IOnItemClick<T>{
        void onClick(T item);
        void onLongClick(T item);
    }

}


