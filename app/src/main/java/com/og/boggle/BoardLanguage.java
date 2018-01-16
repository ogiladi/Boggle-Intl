package com.og.boggle;

// This class creates the list of letters, including accents, and creates the alphabet
// frequency list, which is used to choose a random letter

//frequency lists are taken from
// http://practicalcryptography.com/cryptanalysis/letter-frequencies-various-languages/

import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import static java.util.Arrays.asList;

class BoardLanguage {
    private TreeMap<Character, Integer> alphabet;
    private TreeMap<Character, List<Character>> getAccents;
    private TreeMap<Character, Character> removeAccents;
    private String fileName;
    private boolean hasAccents;
    private int numLetters;
    private double[] freqListAggr;
    enum Language {EN_CA, FR, ES}
    private Language language;

    BoardLanguage(Language lan) {
        double[] freqList;

        language = lan;
        alphabet = new TreeMap<>();
        getAccents = new TreeMap<>();
        removeAccents = new TreeMap<>();
        int ind = 0;

        for(char ch = 'a'; ch <= 'z'; ch++) alphabet.put(ch, ind++);

        switch(lan) {
            case EN_CA: {
                hasAccents = false;
                fileName = "dict_ca.txt";

                freqList = new double[] {
                        0.0855, 0.0160, 0.0316, 0.0387, 0.1210,
                        0.0218, 0.0209, 0.0496, 0.0733, 0.0022,
                        0.0081, 0.0421, 0.0253, 0.0717, 0.0747,
                        0.0207, 0.0010, 0.0633, 0.0673, 0.0894,
                        0.0268, 0.0106, 0.0183, 0.0019, 0.0172,
                        0.0011};

                break;
            }
            case FR: {
                List<Character> accents_a = asList('â', 'à');
                List<Character> accents_c = Collections.singletonList('ç');
                List<Character> accents_e = asList('é', 'è', 'ê', 'ë');
                List<Character> accents_i = asList('î', 'ï');
                List<Character> accents_o = Collections.singletonList('ô');
                List<Character> accents_u = asList('ü', 'ù', 'û');

                getAccents.put('a', accents_a);
                getAccents.put('c', accents_c);
                getAccents.put('e', accents_e);
                getAccents.put('i', accents_i);
                getAccents.put('o', accents_o);
                getAccents.put('u', accents_u);

                hasAccents = true;
                fileName = "dict_fr.txt";
                freqList = new double[] {
                        0.0808, 0.0096, 0.0344, 0.0408, 0.1745,
                        0.0112, 0.0118, 0.0093, 0.0726, 0.0030,
                        0.0016, 0.0586, 0.0278, 0.0732, 0.0546,
                        0.0298, 0.0085, 0.0686, 0.0798, 0.0711,
                        0.0559, 0.0129, 0.0008, 0.0043, 0.0034,
                        0.0010};

                break;
            }
            case ES: {
                List<Character> accents_a = Collections.singletonList('á');
                List<Character> accents_e = Collections.singletonList('é');
                List<Character> accents_i = Collections.singletonList('í');
                List<Character> accents_n = Collections.singletonList('ñ');
                List<Character> accents_o = Collections.singletonList('ó');
                List<Character> accents_u = asList('ú', 'ü');

                getAccents.put('a', accents_a);
                getAccents.put('e', accents_e);
                getAccents.put('i', accents_i);
                getAccents.put('n', accents_n);
                getAccents.put('o', accents_o);
                getAccents.put('u', accents_u);

                hasAccents = true;
                fileName = "dict_es.txt";

                freqList = new double[] {
                        0.1250, 0.0127, 0.0443, 0.0514, 0.1324,
                        0.0079, 0.0117, 0.0081, 0.0691, 0.0045,
                        0.0008, 0.0584, 0.0261, 0.0731, 0.0898,
                        0.0275, 0.0083, 0.0662, 0.0744, 0.0442,
                        0.0400, 0.0098, 0.0003, 0.0019, 0.0079,
                        0.0042};

                break;
            }
            default:
                freqList = null;
        }

        double sum = 0;
        for (double aFreqList : freqList) sum += aFreqList;

        freqListAggr = new double[freqList.length];
        freqListAggr[0] = freqList[0];
        for (int i = 1; i < freqList.length; i++) {
            freqListAggr[i] = freqListAggr[i-1] + freqList[i]/sum;
        }

        if (hasAccents) {
            for (char ch : getAccents.keySet()) {
                for (char accent : getAccents.get(ch)) {
                    alphabet.put(accent, ind++);
                    removeAccents.put(accent, ch);
                }
            }
        }
        numLetters = alphabet.size();
    }

//    Choose a random letter according to the frequency list
    char getRandomLetter() {
        double rand = Math.random();
        int ind = 0;
        while (rand > freqListAggr[ind] && ind < freqListAggr.length) ind++;
        return (char) ('a' + ind);
    }

    String getFileName() {
        return fileName;
    }

    int getNumLetters() {
        return numLetters;
    }

    int getIndex(char ch) {
        return alphabet.get(ch);
    }

    boolean letterHasAccents(char ch) {
        return (hasAccents && getAccents.containsKey(ch));
    }

    List<Character> letterAccents(char ch) {
        if (getAccents.containsKey(ch)) {
            return getAccents.get(ch);
        }
        return null;
    }

//    Return the word with accented letters
    private String removeAccents(String word) {
        StringBuilder accentsRemoves = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (removeAccents.containsKey(ch)) {
                accentsRemoves.append(removeAccents.get(ch));
            } else {
                accentsRemoves.append(ch);
            }
        }
        return accentsRemoves.toString();
    }

//    Compare between an accented word and a non-accented one
    boolean compareWords(BoggleWord boardWord, BoggleWord dictWord) {
        if (language == Language.EN_CA) return boardWord.getWord().equals(dictWord.getWord());
        return boardWord.getWord().equals(removeAccents(dictWord.getWord()));
    }
}
