package com.ad.demo.utils;

import android.widget.TextView;

public class MessageUtil {
    private static int mIndex = 0;

    /*
        public static void Show(final String msg, final int flag, final TextView textView) {
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText("");
                    textView.setText(msg);
                    int offset = textView.getLineCount() * textView.getLineHeight();
                    if (offset > textView.getHeight()) {
                        textView.scrollTo(0, offset - textView.getHeight());
                    }
                }
            });
        }
    */
    public static void List(final String msg, final TextView textView) {
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.append(msg + "\r\n");
                int offset = textView.getLineCount() * textView.getLineHeight();
                if (offset > textView.getHeight()) {
                    textView.scrollTo(0, offset - textView.getHeight());
                }
            }
        });
    }

    public static void Show(final String msg, final TextView textView) {
        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText("");
                if (msg == null) return;
                textView.setText(msg + String.format("(%03d)", mIndex++));
                int offset = textView.getLineCount() * textView.getLineHeight();
                if (offset > textView.getHeight()) {
                    textView.scrollTo(0, offset - textView.getHeight());
                }
            }
        });
    }
}

