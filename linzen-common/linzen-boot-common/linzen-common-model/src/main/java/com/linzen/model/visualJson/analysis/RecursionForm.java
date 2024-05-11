package com.linzen.model.visualJson.analysis;

import com.linzen.model.visualJson.FieLdsModel;
import com.linzen.model.visualJson.TableModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecursionForm {
    private List<FieLdsModel> list;
    private List<TableModel> tableModelList;

}
