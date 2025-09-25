package org.pitest.g2p.core.pos;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagFormat;
import opennlp.tools.postag.POSTaggerME;

import java.util.ArrayList;
import java.util.List;

/**
 * NN 	Noun, singular or mass
 * DT 	Determiner
 * VB 	Verb, base form
 * VBD 	Verb, past tense
 * VBZ 	Verb, third person singular present
 * IN 	Preposition or subordinating conjunction
 * NNP 	Proper noun, singular
 * TO 	to
 * JJ 	Adjective
 */
public class SimplePOSTagger {

    private final POSModel posModel;

    public SimplePOSTagger(POSModel posModel) {
        this.posModel = posModel;
    }


    public List<POSToken> tagWords(String[] tokens) {
        POSTaggerME posTagger = new POSTaggerME(posModel, POSTagFormat.PENN);
        String[] tags = posTagger.tag(tokens);
        List<POSToken> results = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {

            String token = tokens[i];
            Pos pos = Pos.fromString(tags[i]);

            // glue words back together if they end with 's
            if (!results.isEmpty()) {
                POSToken last = results.get(results.size() - 1);
                if (token.equals("'")) {
                    results.remove(last);
                    POSToken combined = new POSToken(last.word() + token, last.pos());
                    results.add(combined);
                    continue;
                }

                if (last.word().endsWith("'")) {
                    results.remove(last);
                    POSToken combined = new POSToken(last.word() + token, last.pos());
                    results.add(combined);
                    continue;
                }

            }

            POSToken result = new POSToken(token, pos);
            results.add(result);
        }

        return results;
    }

}


