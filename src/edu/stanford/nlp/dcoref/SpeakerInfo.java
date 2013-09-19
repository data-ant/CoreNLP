package edu.stanford.nlp.dcoref;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.math.NumberMatchingRegex;
import edu.stanford.nlp.util.Generics;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Information about a speaker
 *
 * @author Angel Chang
 */
public class SpeakerInfo {
  private String speakerId;
  private String speakerName;
  private String[] speakerNameStrings; // tokenized speaker name
  private String speakerDesc;
  // TODO: it is possible for this set to have Mentions with different
  // corefClusterIds.  In that case, the results are actually not
  // deterministic, since the cluster id of the speaker will depend on
  // the order in which mentions are iterated over.  This will change
  // from execution to execution in a HashSet.
  private Set<Mention> mentions = Generics.newHashSet();  // Mentions that corresponds to the speaker...
  private Mention originalMention;            // the mention used when creating this SpeakerInfo
  private boolean speakerIdIsNumber;          // speaker id is a number (probably mention id)
  private boolean speakerIdIsAutoDetermined;  // speaker id was auto determined by system
  private Mention mainMention;

  // TODO: keep track of speaker utterances?

  private static final Pattern DEFAULT_SPEAKER_PATTERN = Pattern.compile("PER\\d+");
  protected static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+|_+");
  public SpeakerInfo(String speakerName, Mention originalMention) {
    this.speakerId = speakerName;
    this.originalMention = originalMention;
    int commaPos = speakerName.indexOf(',');
    if (commaPos > 0) {
      // drop everything after the ,
      this.speakerName = speakerName.substring(0, commaPos);
      if (commaPos < speakerName.length()) {
        speakerDesc = speakerName.substring(commaPos+1);
        speakerDesc = speakerDesc.trim();
        if (speakerDesc.isEmpty()) speakerDesc = null;
      }
    } else {
      this.speakerName = speakerName;
    }
    this.speakerNameStrings = WHITESPACE_PATTERN.split(this.speakerName);
    speakerIdIsNumber = NumberMatchingRegex.isDecimalInteger(speakerId);
    speakerIdIsAutoDetermined = DEFAULT_SPEAKER_PATTERN.matcher(speakerId).matches();
  }

  public boolean hasRealSpeakerName() {
    return mentions.size() > 0 || !(speakerIdIsAutoDetermined || speakerIdIsNumber);
  }

  public String getSpeakerName() {
    return speakerName;
  }

  public String getSpeakerDesc() {
    return speakerDesc;
  }

  public String[] getSpeakerNameStrings() {
    return speakerNameStrings;
  }

  public Set<Mention> getMentions() {
    return mentions;
  }

  public boolean containsMention(Mention m) {
    return mentions.contains(m);
  }

  public void addMention(Mention m) {
    if (mentions.isEmpty() && m.mentionType == Dictionaries.MentionType.PROPER) {
      // check if mention name is probably better indicator of the speaker
      String mentionName = m.spanToString();
      if (speakerIdIsNumber || speakerIdIsAutoDetermined) {
        String nerName = m.nerName();
        speakerName = (nerName != null)? nerName: mentionName;
        speakerNameStrings = WHITESPACE_PATTERN.split(speakerName);
      }
    }
    mentions.add(m);
  }



  public int getCorefClusterId() {
    if (originalMention != null && originalMention.corefClusterID >= 0) {
      return originalMention.corefClusterID;
    }
    int corefClusterId = -1;     // Coref cluster id that corresponds to this speaker
    for (Mention m:mentions) {
      if (m.corefClusterID >= 0) {
        corefClusterId = m.corefClusterID;
        break;
      }
    }
    return corefClusterId;
  }

  public String toString() {
    return speakerId;
  }

}
