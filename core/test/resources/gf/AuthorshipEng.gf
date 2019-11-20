concrete AuthorshipEng of Authorship = {
  lincat
    Sentence, Author, Title, Quality, Event, ModifiedTitle = {s : Str};

  lin
    AuthorshipV1 a t e = {s = t.s ++ "is" ++ e.s ++ "by" ++ a.s};
    AuthorshipV2 a t = {s = a.s ++ "is the author of" ++ t.s};
    TitleWithAdv adv t = {s = adv.s ++ t.s};
    TitleData = {s = "{{TITLE}}"};
    AuthorData = {s = "{{AUTHOR}}"};
    Wrote = {s = "authored" | "written"};
    Good = {s = "excellent" | "good"};
}