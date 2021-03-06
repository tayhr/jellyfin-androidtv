package org.jellyfin.androidtv.presentation;

import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.jellyfin.androidtv.R;
import org.jellyfin.androidtv.TvApp;
import org.jellyfin.androidtv.details.MyDetailsOverviewRow;
import org.jellyfin.androidtv.model.InfoItem;
import org.jellyfin.androidtv.ui.GenreButton;
import org.jellyfin.androidtv.ui.TextUnderButton;
import org.jellyfin.androidtv.util.InfoLayoutHelper;
import org.jellyfin.androidtv.util.Utils;
import org.jellyfin.apiclient.model.dto.BaseItemDto;

import androidx.leanback.widget.RowPresenter;

import com.google.android.flexbox.FlexboxLayout;

public class MyDetailsOverviewRowPresenter extends RowPresenter {

    private ViewHolder viewHolder;

    public final class ViewHolder extends RowPresenter.ViewHolder {
        private FlexboxLayout mGenreRow;
        private LinearLayout mInfoRow;
        private TextView mTitle;
        private ImageView mPoster;
        private TextView mSummary;
        private LinearLayout mButtonRow;
        private ImageView mStudioImage;

        private TextView mInfoTitle1;
        private TextView mInfoTitle2;
        private TextView mInfoTitle3;
        private TextView mInfoValue1;
        private TextView mInfoValue2;
        private TextView mInfoValue3;

        private RelativeLayout mLeftFrame;

        /**
         * Constructor for ViewHolder.
         *
         * @param rootView The View bound to the Row.
         */
        public ViewHolder(View rootView) {
            super(rootView);
            mTitle = (TextView) rootView.findViewById(R.id.fdTitle);
            mTitle.setShadowLayer(5, 5, 5, Color.BLACK);
            mInfoTitle1 = (TextView) rootView.findViewById(R.id.infoTitle1);
            mInfoTitle2 = (TextView) rootView.findViewById(R.id.infoTitle2);
            mInfoTitle3 = (TextView) rootView.findViewById(R.id.infoTitle3);
            mInfoValue1 = (TextView) rootView.findViewById(R.id.infoValue1);
            mInfoValue2 = (TextView) rootView.findViewById(R.id.infoValue2);
            mInfoValue3 = (TextView) rootView.findViewById(R.id.infoValue3);

            mLeftFrame = (RelativeLayout) rootView.findViewById(R.id.leftFrame);

            mGenreRow = (FlexboxLayout) rootView.findViewById(R.id.fdGenreRow);
            mInfoRow =  (LinearLayout)rootView.findViewById(R.id.fdMainInfoRow);
            mPoster = (ImageView) rootView.findViewById(R.id.mainImage);
            //mStudioImage = (ImageView) rootView.findViewById(R.id.studioImage);
            mButtonRow = (LinearLayout) rootView.findViewById(R.id.fdButtonRow);
            mSummary = (TextView) rootView.findViewById(R.id.fdSummaryText);

        }

        public void collapseLeftFrame() {
            ViewGroup.LayoutParams params = mLeftFrame.getLayoutParams();
            params.width = Utils.convertDpToPixel(TvApp.getApplication(),100);
        }

    }

    @Override
    protected ViewHolder createRowViewHolder(ViewGroup parent) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_details_overview_row, parent, false);
        viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    protected void onBindRowViewHolder(RowPresenter.ViewHolder holder, Object item) {
        super.onBindRowViewHolder(holder, item);

        MyDetailsOverviewRow row = (MyDetailsOverviewRow) item;
        ViewHolder vh = (ViewHolder) holder;

        setTitle(row.getItem().getName());
        InfoLayoutHelper.addInfoRow(TvApp.getApplication().getCurrentActivity(), row.getItem(), vh.mInfoRow, false, false);
        addGenres(vh.mGenreRow, row.getItem());
        setInfo1(row.getInfoItem1());
        setInfo2(row.getInfoItem2());
        setInfo3(row.getInfoItem3());

        vh.mPoster.setImageDrawable(row.getImageDrawable());
        //vh.mStudioImage.setImageDrawable(row.getStudioDrawable());

        // Support simple HTML elements
        String summaryRaw = row.getSummary();
        if (summaryRaw != null) {
            Spanned summarySpanned;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                summarySpanned = Html.fromHtml(summaryRaw, Html.FROM_HTML_MODE_COMPACT);
            } else {
                summarySpanned = Html.fromHtml(summaryRaw);
            }

            vh.mSummary.setText(summarySpanned);
        }

        switch (row.getItem().getBaseItemType()) {
            case Person:
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vh.mSummary.getLayoutParams();
                params.topMargin = 10;
                params.height = Utils.convertDpToPixel(TvApp.getApplication(), 185);
                vh.mSummary.setMaxLines(9);
                vh.mGenreRow.setVisibility(View.GONE);
                vh.mInfoRow.setVisibility(View.GONE);
                vh.collapseLeftFrame();

                break;
        }



        vh.mButtonRow.removeAllViews();
        for (TextUnderButton button : row.getActions()) {
            vh.mButtonRow.addView(button);
        }

    }

    private void addGenres(FlexboxLayout layout, BaseItemDto item) {
        layout.removeAllViews();
        if (item.getGenres() != null && item.getGenres().size() > 0) {
            boolean first = true;
            for (String genre : item.getGenres()) {
                if (!first) InfoLayoutHelper.addSpacer(TvApp.getApplication().getCurrentActivity(), layout, "  /  ", 12);
                first = false;
                layout.addView(new GenreButton(TvApp.getApplication().getCurrentActivity(), 14, genre, item.getBaseItemType()));
            }
        }
    }



    public void setTitle(String text) {
        viewHolder.mTitle.setText(text);
        if (text.length() > 28) {
            // raise it up a bit
            ((RelativeLayout.LayoutParams)viewHolder.mTitle.getLayoutParams()).topMargin = Utils.convertDpToPixel(TvApp.getApplication(), 55);
        }
    }

    public void setInfo1(InfoItem info) {
        if (info == null) {
            viewHolder.mInfoTitle1.setText("");
            viewHolder.mInfoValue1.setText("");
        } else {
            viewHolder.mInfoTitle1.setText(info.getLabel());
            viewHolder.mInfoValue1.setText(info.getValue());
        }
    }

    public void setInfo2(InfoItem info) {
        if (info == null) {
            viewHolder.mInfoTitle2.setText("");
            viewHolder.mInfoValue2.setText("");
        } else {
            viewHolder.mInfoTitle2.setText(info.getLabel());
            viewHolder.mInfoValue2.setText(info.getValue());
        }
    }

    public void setInfo3(InfoItem info) {
        if (info == null) {
            viewHolder.mInfoTitle3.setText("");
            viewHolder.mInfoValue3.setText("");
        } else {
            viewHolder.mInfoTitle3.setText(info.getLabel());
            viewHolder.mInfoValue3.setText(info.getValue());
        }
    }


    public LinearLayout getButtonRow() { return viewHolder.mButtonRow; }
    public ImageView getPosterView() { return viewHolder.mPoster; }
    public FlexboxLayout getGenreRow() { return viewHolder.mGenreRow; }
    public TextView getSummaryView() { return viewHolder.mSummary; }
    public LinearLayout getInfoRow() { return viewHolder.mInfoRow; }

    public void updateEndTime(String text) {
        if (viewHolder != null && viewHolder.mInfoValue3 != null) viewHolder.mInfoValue3.setText(text);
    }
}
