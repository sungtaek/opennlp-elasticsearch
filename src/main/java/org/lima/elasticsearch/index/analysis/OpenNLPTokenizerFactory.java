package org.lima.elasticsearch.index.analysis;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.lima.elasticsearch.tokenizer.OpenNLPTokenizer;

public class OpenNLPTokenizerFactory extends AbstractTokenizerFactory {
	private String tokenizerModel;
	private String taggerModel;

	public OpenNLPTokenizerFactory(IndexSettings indexSettings, String name, Settings settings, String pluginPath) {
		super(indexSettings, name, settings);
		this.tokenizerModel =  pluginPath + File.separator + OpenNLPTokenizer.name
				+ File.separator + "model" + File.separator + settings.get("tokenizerModel", "en-token.bin");
		this.taggerModel =  pluginPath + File.separator + OpenNLPTokenizer.name
				+ File.separator + "model" + File.separator + settings.get("taggerModel", "en-pos-maxent.bin");
	}

	@Override
	public Tokenizer create() {
		try {
			System.out.println("tokenizerModel: " + tokenizerModel);
			System.out.println("taggerModel: " + taggerModel);
			return new OpenNLPTokenizer(tokenizerModel, taggerModel);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
