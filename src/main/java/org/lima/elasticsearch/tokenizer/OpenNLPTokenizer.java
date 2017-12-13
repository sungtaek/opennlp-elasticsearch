package org.lima.elasticsearch.tokenizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

public class OpenNLPTokenizer extends Tokenizer {
	public static String name = "opennlp_tokenizer";
	
	private TokenizerME tokenizer;
	private POSTaggerME tagger;
	
	String tokens[];
	String tags[];
	int idx;
	
	private CharTermAttribute charTermAttribute;
	private TypeAttribute typeAttribute;
	
	public OpenNLPTokenizer(String tokenizerModel, String taggerModel) throws IOException {
		TokenizerModel tModel;
		tModel = new TokenizerModel(new FileInputStream(tokenizerModel));
		this.tokenizer = new TokenizerME(tModel);

		POSModel pModel = new POSModelLoader().load(new File(taggerModel));
		this.tagger = new POSTaggerME(pModel);
		
		tokens = null;
		tags = null;
		idx = 0;

		this.charTermAttribute = addAttribute(CharTermAttribute.class);
		this.typeAttribute = addAttribute(TypeAttribute.class);
	}

	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
		
		// read document & tagging
		if(tokens == null) {
			String doc = getDocument();
			tokens = tokenizer.tokenize(doc);
			tags = tagger.tag(tokens);
			idx = 0;
		}
		
		// set token/tag
		if(tokens != null && tokens.length > idx) {
			charTermAttribute.append(tokens[idx]);
			typeAttribute.setType(tags[idx]);
			idx++;
			return true;
		}
		
		initValues();
		return false;
	}
	
	@Override
	public final void reset() throws IOException {
		super.reset();
		initValues();
	}
	
	private void initValues() {
		tokens = null;
		tags = null;
		idx = 0;
	}

	private String getDocument() throws IOException {
		StringBuilder doc = new StringBuilder();
		char[] tmp = new char[1024];
		int len;
		while((len = input.read(tmp)) != -1) {
			doc.append(new String(tmp, 0, len));
		}
		return doc.toString();
	}
}
