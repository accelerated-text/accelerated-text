abstract MadeOf = {
  cat Message;
      Subject; Object;
      SubjectLex; ObjectLex;

  fun
    MadeOfSentence : Subject -> Object -> Message;

    SimpleSubject : SubjectLex -> Subject;
    SimpleObject : ObjectLex -> Object;

    SubjectData : SubjectLex;
    ObjectData : ObjectLex;
}
