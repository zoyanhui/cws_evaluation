/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.evaluation.impl;

import org.apdplat.evaluation.Evaluation;
import org.apdplat.evaluation.EvaluationResult;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.SegmentationAlgorithm;
import org.apdplat.word.segmentation.Word;
import org.apdplat.word.tagging.PartOfSpeechTagging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * word分词器分词效果评估
 * @author 杨尚川
 */
public class WordTaggingEvaluation extends Evaluation implements org.apdplat.evaluation.WordSegmenter{
    @Override
    public List<EvaluationResult> run() throws Exception {
        List<EvaluationResult> list = new ArrayList<>();
        for(SegmentationAlgorithm segmentationAlgorithm : SegmentationAlgorithm.values()){
            System.out.println("开始评估 word分词 "+segmentationAlgorithm.getDes());
            list.add(run(segmentationAlgorithm));
            //每评估完一种算法就保存一次报告
            Evaluation.generateReport(list, "word分词器分词效果评估报告.txt");
        }
        return list;
    }
    private EvaluationResult run(SegmentationAlgorithm segmentationAlgorithm) throws Exception{
        //对文本进行分词
        String resultText = "temp/result-text-"+segmentationAlgorithm.name()+".txt";
        float rate = segFile(testText, resultText, text -> WordTaggingEvaluation.seg(text, segmentationAlgorithm));
        //对分词结果进行评估
        EvaluationResult evaluationResult = evaluate(resultText, standardText);
        evaluationResult.setAnalyzer("word分词 "+segmentationAlgorithm.getDes());
        evaluationResult.setSegSpeed(rate);
        return evaluationResult;
    }
    @Override
    public Map<String, String> segMore(String text) {
        Map<String, String> map = new HashMap<>();
        for(SegmentationAlgorithm segmentationAlgorithm : SegmentationAlgorithm.values()){
            map.put(segmentationAlgorithm.getDes(), seg(text, segmentationAlgorithm));
        }
        return map;
    }
    private static String seg(String text, SegmentationAlgorithm segmentationAlgorithm) {
        StringBuilder result = new StringBuilder();
        List<Word> words = WordSegmenter.segWithStopWords(text, segmentationAlgorithm);
        PartOfSpeechTagging.process(words);
        words.stream()
                .forEach(word -> result.append(word.getText()).append(" "));
        return result.toString();
    }
    public static void main(String[] args) throws Exception{
        new WordTaggingEvaluation().run();
    }
}