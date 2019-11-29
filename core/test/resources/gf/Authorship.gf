-- Document. S ::= x02;
-- Segment. x02 ::= x03;
-- CF Version
-- AuthorV1. x03 ::= x05 "is" "the author of" x07;
-- AuthorV2. x03 ::= x07 "is" x04 "by" x05;
-- Item. x04 ::= "authored";
-- Item. x04 ::= "written";
-- DataMod. x05 ::= x06 "{{authors}}";
-- Item. x06 ::= "excellent";
-- Item. x06 ::= "good";
-- Data. x07 ::= "{{title}}";

abstract Authorship = {
  flags
    startcat = Sentence;
  cat
    Sentence; Author; Title; Quality; Event; ModifiedTitle;
  fun
    AuthorshipV1 : Author -> ModifiedTitle -> Event -> Sentence;
    AuthorshipV2 : Author -> ModifiedTitle -> Sentence;
    AuthorshipV3 : Author -> ModifiedTitle -> Sentence;
    TitleWithAdv : Quality -> Title -> ModifiedTitle;
    Wrote : Event;
    Good : Quality;
    AuthorData : Author;
    TitleData : Title;
}