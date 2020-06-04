package ncist.wl171.no201701024118.lastproject;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;


public class WebLinkMethod extends LinkMovementMethod {



        private static WebLinkMethod instance;
        private Context context;

        private WebLinkMethod(Context context) {//传入avticity
            this.context = context;
        }

        public static MovementMethod getInstance(Context context) {//重写
            if (instance == null)
                instance = new WebLinkMethod(context);
            return instance;
        }

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {//重写onTouchEvent()
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);
//          ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
                URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
                if (link.length != 0) {

                    if (action == MotionEvent.ACTION_UP) {
//                  link[0].onClick(widget);
                        Intent intent = new Intent(context, newActivity.class);
                        intent.putExtra("url", link[0].getURL());
                        System.out.println("URL="+link[0].getURL());
                        context.startActivity(intent);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }
                    return true;
                } else {
                    Selection.removeSelection(buffer);
                }
            }
            return super.onTouchEvent(widget, buffer, event);
        }
    }
































































