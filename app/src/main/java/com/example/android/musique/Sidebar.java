package com.example.android.musique;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
class Sidebar extends View {
    float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getResources().getDisplayMetrics());
    private final int m_nItemHeight = (int) px;
    private char[] l;
    private SectionIndexer sectionIndexer = null;
    private ListView list;
    public Sidebar(Context context) {
        super(context);
        init();
    }
    public Sidebar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Sidebar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        l = new char[] { '#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
                'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
        setBackgroundColor(0x44FFFFFF);
    }

    public void setListView(ListView _list) {
        list = _list;
        sectionIndexer = (SectionIndexer) _list.getAdapter();
    }
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int i = (int) event.getY();
        int idx = i / m_nItemHeight;
        if (idx >= l.length) {
            idx = l.length - 1;
        } else if (idx < 0) {
            idx = 0;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            if (sectionIndexer == null) {
                sectionIndexer = (SectionIndexer) list.getAdapter();
            }
            int position = sectionIndexer.getPositionForSection(l[idx]);
            if (position == -1) {
                return true;
            }
            list.setSelection(position);
        }
        return true;
    }
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(0xFFA6A9AA);
        paint.setTextSize(24);
        paint.setTextAlign(Paint.Align.CENTER);
        float widthCenter = getMeasuredWidth() / 2;
        for (int i = 0; i < l.length; i++) {
            canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight + (i * m_nItemHeight), paint);
        }
        super.onDraw(canvas);
    }
}