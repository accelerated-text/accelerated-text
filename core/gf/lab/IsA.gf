abstract IsA = {
  cat Message;
      SubjectLex; AttributeLex;
      SubjectModifier1; SubjectModifier2; SubjectModifier;
      AttributeModifier;
      SubjectModifierLex1; SubjectModifierLex2; AttributeModifierLex;
      Subject; Attribute;

  fun
    IsASentence : Subject -> Attribute -> Message;

    ModifiedSubject : SubjectModifier -> SubjectLex -> Subject;

    ModifiedAttribute : AttributeModifierLex -> AttributeLex -> Attribute;

    SimpleSubject : SubjectLex -> Subject;
    SimpleAttribute : AttributeLex -> Attribute;

    SimpleSubjectModifier1: SubjectModifierLex1 -> SubjectModifier1;
    SimpleSubjectModifier2: SubjectModifierLex2 -> SubjectModifier2;
    VeryModifiedSubject : SubjectModifier1 -> SubjectModifier2 -> SubjectModifier;

    SimpleAttributeModifier : AttributeModifierLex -> AttributeModifier;

    SubjectData: SubjectLex;
    AttributeData: AttributeLex;

    SubjectModData1: SubjectModifierLex1;
    SubjectModData2: SubjectModifierLex2;
    AttributeModData: AttributeModifierLex;
}
