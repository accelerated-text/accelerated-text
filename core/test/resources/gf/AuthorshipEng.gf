concrete AuthorshipEng of Authorship = open LangFunctionsEng, DictEng in {
  lincat
    Sentence, Author, Title, Quality, Event, ModifiedTitle = {s : Str};

  lin
    AuthorshipV1 a t e = {s = t.s ++ (copula Sg).s ++ e.s ++ "by" ++ a.s};
    AuthorshipV2 a t = {s = (mkCopula a { s = "the author"}  Sg).s ++ "of" ++ t.s};
    TitleWithAdv adv t = {s = adv.s ++ t.s};
    TitleData = {s = "{{TITLE}}"};
    AuthorData = {s = "{{AUTHOR}}"};
    Wrote = { s = write_V.s ! VPast };
    Good = {s = "excellent" | "good"};
}