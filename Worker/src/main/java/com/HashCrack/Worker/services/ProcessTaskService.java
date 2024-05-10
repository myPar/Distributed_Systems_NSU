package com.HashCrack.Worker.services;

import com.HashCrack.Worker.controllers.ManagerRequestController;
import com.HashCrack.Worker.dto.ManagerTaskDTO;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.paukov.combinatorics3.Generator;

import java.lang.Math;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessTaskService {
    private Logger logger = LoggerFactory.getLogger(ProcessTaskService.class);
    private String md5Hash;
    private final AtomicInteger counter;
    private String foundWord;

    public ProcessTaskService() {
        counter = new AtomicInteger();
    }

    @NoArgsConstructor
    public static class ProcessTaskServiceException extends RuntimeException {
        public ProcessTaskServiceException(String message) {
            super(message);
        }
    }
    private static final Character[] alphabet = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };
    @Builder
    private static class TaskInfo {
        private int startLength;
        private int prefixSetPower;
        private int startIdx;
        private int wordsCount;
    }
    private TaskInfo calcTaskInfo(ManagerTaskDTO task) {
        int resultLength = 1;
        int postfixSetPower = (int) Math.pow(alphabet.length, resultLength);
        int prefixSetPower = 0;
        int startIdx = task.getStartIndex();

        for (int i = 0; i <= startIdx; i++) {
            if (i >= postfixSetPower) {
                prefixSetPower = postfixSetPower;
                resultLength++;
                postfixSetPower += (int) Math.pow(alphabet.length, resultLength);
            }
        }
        return new TaskInfo(resultLength, prefixSetPower, startIdx, task.getWordsCount());
    }

    private void checkWordHash(List<Character> word) throws ProcessTaskServiceException {
        StringBuilder wordBuilder = new StringBuilder();
        for (Character ch: word) {
            wordBuilder.append(ch.charValue());
        }
        String wordString = wordBuilder.toString();
        MessageDigest md5hashing;
        try {
            md5hashing = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new ProcessTaskServiceException(e.getMessage());
        }

        md5hashing.update(wordString.getBytes());                     // calc hash
        String hashValue = Hex.encodeHexString(md5hashing.digest()); // get hash string in hex format

        if (hashValue.equals(md5Hash)) {
            foundWord = wordString;
        }
        counter.set(counter.get() + 1); // new word parsed - increment counter
    }

    // returns found word if hashes are equal, and empty string otherwise
    public String processTask(ManagerTaskDTO task) throws ProcessTaskServiceException {
        TaskInfo taskInfo = calcTaskInfo(task);

        int stIdx = task.getStartIndex();
        int skipCount = stIdx - taskInfo.prefixSetPower;
        int curWordLength = taskInfo.startLength;

        Stream<List<Character>> currentSet = Generator
                                             .combination(Arrays.asList(alphabet))
                                             .multi(curWordLength)
                                             .stream()
                                             .skip(skipCount)
                                             .limit(taskInfo.wordsCount);
        counter.set(0);   // use just as wrapper class for int counter
        md5Hash = task.getMd5Hash();
        foundWord = ""; // empty at start

        do {
            currentSet.forEach(this::checkWordHash);
            // update set
            curWordLength++;
            currentSet = Generator
                    .combination(Arrays.asList(alphabet))
                    .multi(curWordLength)
                    .stream()
                    .limit(taskInfo.wordsCount - counter.get());
        }
        while (counter.get() < taskInfo.wordsCount && foundWord.length() == 0);

        return foundWord;
    }
}
