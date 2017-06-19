package pl.witoldbrzezinski.esemesolo.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import pl.witoldbrzezinski.esemesolo.R;
import pl.witoldbrzezinski.esemesolo.model.SmsModel;

/**
 * Created by Wiciu on 17.06.2017.
 */

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SmsModel> mConversationSmsList;
    Context mContext;

    public ConversationAdapter(List<SmsModel> converationSmses) {
        mConversationSmsList = converationSmses;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case 1:
                View leftView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item_out,parent,false);
                return new ConversationOutHolder(leftView);
            case 2: View rightView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_item_in,parent,false);
                return new ConversationInHolder(rightView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 1:
                mContext = holder.itemView.getContext();
                SimpleDateFormat outSimpleDataFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                SmsModel outSmsModel = mConversationSmsList.get(position);
                ((ConversationOutHolder) holder).outMessage.setText(outSmsModel.getMessageText());
                ((ConversationOutHolder)holder).outDate.setText(outSimpleDataFormat.format(outSmsModel.getSmsDate()));
                break;
            case 2:
                mContext = holder.itemView.getContext();
                SimpleDateFormat inSimpleDataFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                SmsModel inSmsModel = mConversationSmsList.get(position);
                ((ConversationInHolder) holder).inMessage.setText(inSmsModel.getMessageText());
                ((ConversationInHolder)holder).inDate.setText(inSimpleDataFormat.format(inSmsModel.getSmsDate()));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        String type = mConversationSmsList.get(position).getType();
        if (type.equals("1")){
            return 1;
        } else if(type.equals("2")){
            return 2;
        }
        else return -1;
    }

    @Override
    public int getItemCount() {
        return mConversationSmsList.size();
    }

    class ConversationInHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView inMessage;
        TextView inDate;

        public ConversationInHolder(View itemView) {
            super(itemView);
            inMessage = itemView.findViewById(R.id.sms_message_in);
            inDate = itemView.findViewById(R.id.sms_date_in);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    class ConversationOutHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView outMessage;
        TextView outDate;

        public ConversationOutHolder(View itemView) {
            super(itemView);
            outMessage = itemView.findViewById(R.id.sms_message_out);
            outDate = itemView.findViewById(R.id.sms_date_out);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

        }
    }

}
