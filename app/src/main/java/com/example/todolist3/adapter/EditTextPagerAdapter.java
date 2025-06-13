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

import com.example.todolist3.LockScreenActivity;
import com.example.todolist3.R;
import com.example.todolist3.db.TodoRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.Toast;

public class EditTextPagerAdapter extends RecyclerView.Adapter<EditTextPagerAdapter.EditTextViewHolder> {

    private final int pageCount;
    private String[] editTextContents;
    private String[] dueDateContents;
    private OnPageContentChangeListener contentChangeListener;

    public interface OnPageContentChangeListener {
        void onPageContentChanged(int position, boolean isEmpty);
        void onPageFull(int position);
        void onTaskSaved(int position);
    }

    public EditTextPagerAdapter(int pageCount) {
        this.pageCount = pageCount;
        // 페이지당 5개의 EditText가 있으므로 총 개수는 pageCount * 5
        this.editTextContents = new String[pageCount * 5];
        this.dueDateContents = new String[pageCount * 5];
        // 기본값으로 빈 문자열 초기화
        for (int i = 0; i < editTextContents.length; i++) {
            editTextContents[i] = "";
            dueDateContents[i] = "날짜";  // 기본값은 "날짜"
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
        int startIdx = position * 5;

        holder.editText1.setText(editTextContents[startIdx]);
        holder.dateButton1.setText(dueDateContents[startIdx]);

        holder.editText2.setText(editTextContents[startIdx + 1]);
        holder.dateButton2.setText(dueDateContents[startIdx + 1]);

        holder.editText3.setText(editTextContents[startIdx + 2]);
        holder.dateButton3.setText(dueDateContents[startIdx + 2]);

        holder.editText4.setText(editTextContents[startIdx + 3]);
        holder.dateButton4.setText(dueDateContents[startIdx + 3]);

        holder.editText5.setText(editTextContents[startIdx + 4]);
        holder.dateButton5.setText(dueDateContents[startIdx + 4]);


        // 각 EditText의 변경 감지
        setupTextChangeListener(holder.editText1, startIdx, holder.dateButton1);
        setupTextChangeListener(holder.editText2, startIdx + 1, holder.dateButton2);
        setupTextChangeListener(holder.editText3, startIdx + 2, holder.dateButton3);
        setupTextChangeListener(holder.editText4, startIdx + 3, holder.dateButton4);
        setupTextChangeListener(holder.editText5, startIdx + 4, holder.dateButton5);
        // 날짜 버튼에 클릭 리스너 추가
        setupDateButtonClickListener(holder.dateButton1, startIdx, holder.itemView.getContext());
        setupDateButtonClickListener(holder.dateButton2, startIdx + 1, holder.itemView.getContext());
        setupDateButtonClickListener(holder.dateButton3, startIdx + 2, holder.itemView.getContext());
        setupDateButtonClickListener(holder.dateButton4, startIdx + 3, holder.itemView.getContext());
        setupDateButtonClickListener(holder.dateButton5, startIdx + 4, holder.itemView.getContext());

        setupSaveButtonClickListener(holder.completeButton1, holder.editText1, holder.dateButton1, holder.itemView.getContext());
        setupSaveButtonClickListener(holder.completeButton2, holder.editText2, holder.dateButton2, holder.itemView.getContext());
        setupSaveButtonClickListener(holder.completeButton3, holder.editText3, holder.dateButton3, holder.itemView.getContext());
        setupSaveButtonClickListener(holder.completeButton4, holder.editText4, holder.dateButton4, holder.itemView.getContext());
        setupSaveButtonClickListener(holder.completeButton5, holder.editText5, holder.dateButton5, holder.itemView.getContext());
    }

    private void setupDateButtonClickListener(final Button dateButton,final int contentIndex, final Context context) {
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 날짜 가져오기
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // DatePickerDialog 생성 및 표시
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // 날짜가 선택되면 버튼 텍스트 업데이트 (월은 0부터 시작하므로 +1)
                                Calendar selectedDate = Calendar.getInstance();
                                selectedDate.set(year, month, dayOfMonth);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("M.dd", Locale.getDefault());
                                String formattedDate = dateFormat.format(selectedDate.getTime());
                                dateButton.setText(formattedDate);
                                dueDateContents[contentIndex] = formattedDate;  // 날짜 배열에 저장
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void setupTextChangeListener(EditText editText, final int contentIndex, final Button dateButton) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 이전에 비어있었고, 이제 텍스트가 입력됐다면 날짜 업데이트
                String previousText = editTextContents[contentIndex];
                if ((previousText == null || previousText.trim().isEmpty()) && !s.toString().trim().isEmpty()) {
                    updateDateButton(dateButton, contentIndex);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                editTextContents[contentIndex] = s.toString();

                // 현재 페이지 위치 확인
                int pagePosition = contentIndex / 5;

                // 현재 페이지가 비어 있는지 확인
                boolean isEmpty = isPageEmpty(pagePosition);

                // 현재 페이지가 모두 채워졌는지 확인
                boolean isFull = isPageFull(pagePosition);

                // 리스너에게 알림
                if (contentChangeListener != null) {
                    // 페이지 상태 변경 알림
                    contentChangeListener.onPageContentChanged(pagePosition, isEmpty);

                    // 페이지가 가득 찼고 마지막 페이지라면 새 페이지 추가 요청
                    if (isFull && pagePosition == pageCount - 1) {
                        contentChangeListener.onPageFull(pagePosition);
                    }
                }
            }
        });
    }
    // 완료 버튼 설정 메소드
    private void setupCompleteButtonClickListener(Button completeButton, final EditText editText,
                                                  final Context context) {
        completeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString().trim();
                if (!content.isEmpty()) {
                    // 할 일 ID 가져오기 (이 로직은 실제 구현에 맞게 수정 필요)
                    int position = getPosition(editText);
                    if (position != -1) {
                        TodoRepository todoRepository = new TodoRepository(context);
                        // DB에서 해당 내용과 일치하는 할 일 찾아서 완료 처리
                        int updatedCount = todoRepository.completeTaskByContent(content);

                        if (updatedCount > 0) {
                            Toast.makeText(context, "할 일을 완료했습니다.", Toast.LENGTH_SHORT).show();

                            // 데이터를 다시 조회하여 페이지 갱신
                            if (context instanceof LockScreenActivity) {
                                ((LockScreenActivity) context).refreshTaskList();
                            }
                        } else {
                            Toast.makeText(context, "완료 처리에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    // 날짜 버튼을 현재 날짜로 업데이트하는 메서드
    private void updateDateButton(Button dateButton,int contentIndex) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M.dd", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        dateButton.setText(currentDate);
        dueDateContents[contentIndex] = currentDate;
    }

    // 특정 페이지의 모든 EditText가 비어있는지 확인하는 메서드
    public boolean isPageEmpty(int position) {
        if (position >= 0 && position < pageCount) {
            int startIdx = position * 5;
            for (int i = 0; i < 5; i++) {
                if (editTextContents[startIdx + i] != null && !editTextContents[startIdx + i].trim().isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    // 페이지의 모든 항목이 채워졌는지 확인하는 메소드
    public boolean isPageFull(int position) {
        if (position >= 0 && position < pageCount) {
            int startIdx = position * 5;
            for (int i = 0; i < 5; i++) {
                if (editTextContents[startIdx + i] == null || editTextContents[startIdx + i].trim().isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return pageCount;
    }

    public void setExistingTasks(List<TodoRepository.Task> tasks) {
        // 모든 데이터 초기화
        for (int i = 0; i < editTextContents.length; i++) {
            editTextContents[i] = "";
            dueDateContents[i] = "날짜";
        }

        // 기존 할일 데이터와 날짜를 적절한 위치에 배치
        for (int i = 0; i < tasks.size() && i < editTextContents.length; i++) {
            TodoRepository.Task task = tasks.get(i);
            editTextContents[i] = task.getContent();

            // 날짜 데이터가 있는 경우에만 설정
            if (task.getDueDate() != null && !task.getDueDate().isEmpty() && !task.getDueDate().equals("null")) {
                dueDateContents[i] = task.getDueDate();
            }
        }

        // 변경 사항 알림
        notifyDataSetChanged();
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

    // 이미 존재하는 할 일인지 확인하는 메소드
    private boolean isNewTask(String content) {
        // editTextContents 배열에 있는지 체크
        for (int i = 0; i < editTextContents.length; i++) {
            // 배열에 있는 텍스트와 정확히 일치하면 이미 존재하는 할 일로 간주
            if (content.equals(editTextContents[i])) {
                return false;
            }
        }
        return true;
    }
    // 저장 버튼 설정 메소드
    private void setupSaveButtonClickListener(Button saveButton, final EditText editText,
                                              final Button dateButton, final Context context) {
        // 이미 저장된 할 일이라면 버튼 텍스트를 "완료"로 변경
        String content = editText.getText().toString().trim();
        if (!content.isEmpty() && !isNewTask(content)) {
            saveButton.setText("완료");
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString().trim();
                String dueDate = dateButton.getText().toString();

                // 내용이 비어있지 않은 경우만 저장
                if (!content.isEmpty()) {
                    TodoRepository todoRepository = new TodoRepository(context);

                    // 버튼 텍스트가 "저장"인 경우 (새 할 일)
                    if (saveButton.getText().toString().equals("저장")) {
                        long taskId = todoRepository.addTask(content, dueDate);

                        if (taskId != -1) {
                            // 성공적으로 저장되었을 때 버튼 텍스트 변경
                            saveButton.setText("완료");
                            Toast.makeText(context, "할 일이 저장되었습니다.", Toast.LENGTH_SHORT).show();

                            // 저장 이벤트를 리스너에 알림
                            int position = getPosition(editText) / 5; // 페이지 위치 계산
                            if (contentChangeListener != null) {
                                contentChangeListener.onTaskSaved(position);
                            }
                        } else {
                            Toast.makeText(context, "저장 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // 버튼 텍스트가 "완료"인 경우 (기존 할 일 완료)
                    else {
                        // 할 일 완료 처리
                        int updatedCount = todoRepository.completeTaskByContent(content);

                        if (updatedCount > 0) {
                            Toast.makeText(context, "할 일을 완료했습니다.", Toast.LENGTH_SHORT).show();

                            // 데이터를 다시 조회하여 페이지 갱신
                            if (context instanceof LockScreenActivity) {
                                ((LockScreenActivity) context).refreshTaskList();
                            }
                        } else {
                            Toast.makeText(context, "완료 처리에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int getPosition(EditText editText) {
        for (int i = 0; i < pageCount; i++) {
            int startIdx = i * 5;
            EditTextViewHolder holder = (EditTextViewHolder)
                    ((RecyclerView) editText.getParent().getParent().getParent()).findViewHolderForAdapterPosition(i);

            if (holder != null) {
                if (editText == holder.editText1) return startIdx;
                if (editText == holder.editText2) return startIdx + 1;
                if (editText == holder.editText3) return startIdx + 2;
                if (editText == holder.editText4) return startIdx + 3;
                if (editText == holder.editText5) return startIdx + 4;
            }
        }
        return -1;
    }


}