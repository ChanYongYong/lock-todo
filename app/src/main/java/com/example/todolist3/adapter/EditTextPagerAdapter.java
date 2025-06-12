package com.example.todolist3.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist3.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditTextPagerAdapter extends RecyclerView.Adapter<EditTextPagerAdapter.EditTextViewHolder> {

    private final int pageCount;
    private String[][] pageContents; // 각 페이지마다 5개의 EditText 내용 저장
    private OnPageContentChangeListener contentChangeListener;

    public interface OnPageContentChangeListener {
        void onPageContentChanged(int position, boolean isEmpty);
    }

    public EditTextPagerAdapter(int pageCount) {
        this.pageCount = pageCount;
        this.pageContents = new String[pageCount][5];
        // 기본값으로 빈 문자열 초기화
        for (int i = 0; i < pageCount; i++) {
            for (int j = 0; j < 5; j++) {
                pageContents[i][j] = "";
            }
        }
    }

    public void setOnPageContentChangeListener(OnPageContentChangeListener listener) {
        this.contentChangeListener = listener;
    }

    @NonNull
    @Override
    public EditTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.page_edittext, parent, false);
        return new EditTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditTextViewHolder holder, int position) {
        // 각 EditText에 저장된 내용 설정
        holder.editText1.setText(pageContents[position][0]);
        holder.editText2.setText(pageContents[position][1]);
        holder.editText3.setText(pageContents[position][2]);
        holder.editText4.setText(pageContents[position][3]);
        holder.editText5.setText(pageContents[position][4]);

        // 각 EditText의 변경 감지
        setupTextChangeListener(holder.editText1, position, 0, holder.dateButton1);
        setupTextChangeListener(holder.editText2, position, 1, holder.dateButton2);
        setupTextChangeListener(holder.editText3, position, 2, holder.dateButton3);
        setupTextChangeListener(holder.editText4, position, 3, holder.dateButton4);
        setupTextChangeListener(holder.editText5, position, 4, holder.dateButton5);
    }

    private void setupTextChangeListener(EditText editText, final int pagePosition, final int editTextIndex, final Button dateButton) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 이전에 비어있었고, 이제 텍스트가 입력됐다면 날짜 업데이트
                String previousText = pageContents[pagePosition][editTextIndex];
                if ((previousText == null || previousText.trim().isEmpty()) && !s.toString().trim().isEmpty()) {
                    updateDateButton(dateButton);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                pageContents[pagePosition][editTextIndex] = s.toString();

                // 현재 페이지의 모든 EditText가 비어있는지 확인
                boolean isEmpty = isPageEmpty(pagePosition);

                // 리스너에게 알림
                if (contentChangeListener != null) {
                    contentChangeListener.onPageContentChanged(pagePosition, isEmpty);
                }
            }
        });
    }

    // 날짜 버튼을 현재 날짜로 업데이트하는 메서드
    private void updateDateButton(Button dateButton) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M.dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        dateButton.setText(currentDate);
    }

    // 특정 페이지의 모든 EditText가 비어있는지 확인하는 메서드
    public boolean isPageEmpty(int position) {
        if (position >= 0 && position < pageCount) {
            for (int i = 0; i < 5; i++) {
                if (pageContents[position][i] != null && !pageContents[position][i].trim().isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public int getItemCount() {
        return pageCount;
    }

    static class EditTextViewHolder extends RecyclerView.ViewHolder {
        EditText editText1, editText2, editText3, editText4, editText5;
        Button dateButton1, dateButton2, dateButton3, dateButton4, dateButton5;
        Button completeButton1, completeButton2, completeButton3, completeButton4, completeButton5;

        public EditTextViewHolder(@NonNull View itemView) {
            super(itemView);
            editText1 = itemView.findViewById(R.id.editText1);
            editText2 = itemView.findViewById(R.id.editText2);
            editText3 = itemView.findViewById(R.id.editText3);
            editText4 = itemView.findViewById(R.id.editText4);
            editText5 = itemView.findViewById(R.id.editText5);

            dateButton1 = itemView.findViewById(R.id.dateButton1);
            dateButton2 = itemView.findViewById(R.id.dateButton2);
            dateButton3 = itemView.findViewById(R.id.dateButton3);
            dateButton4 = itemView.findViewById(R.id.dateButton4);
            dateButton5 = itemView.findViewById(R.id.dateButton5);

            completeButton1 = itemView.findViewById(R.id.completeButton1);
            completeButton2 = itemView.findViewById(R.id.completeButton2);
            completeButton3 = itemView.findViewById(R.id.completeButton3);
            completeButton4 = itemView.findViewById(R.id.completeButton4);
            completeButton5 = itemView.findViewById(R.id.completeButton5);
        }
    }
}
