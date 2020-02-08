abstract IsA = {
  cat Message;
      Subject; Attribute;

  fun
    IsASentence : Subject -> Attribute -> Message;

    SubjectData: Subject;
    AttributeData: Attribute;
}
