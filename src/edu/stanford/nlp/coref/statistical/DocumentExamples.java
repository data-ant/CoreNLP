package edu.stanford.nlp.coref.statistical;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DocumentExamples implements Serializable {
  private static final long serialVersionUID = -2474306699767791493L;

  public final int id;
  public List<Example> examples;
  public final Map<Integer, CompressedFeatureVector> mentionFeatures;

  public DocumentExamples(int id, List<Example> examples,
      Map<Integer, CompressedFeatureVector> mentionFeatures) {
    this.id = id;
    this.examples = examples;
    this.mentionFeatures = mentionFeatures;
  }
}
