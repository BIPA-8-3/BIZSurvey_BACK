package com.bipa.bizsurvey.domain.survey.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private List<ChartAndTextResponse> chartAndTextResults = new ArrayList<>();
    private List<FileResultResponse> fileResults = new ArrayList<>();
}
