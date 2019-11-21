concrete GoodBookEng of GoodBook = {
  lincat
    Sentence = {s : Str};
    Data = {s : Str};
    Modifier = {s : Str};
  lin
    GoodTitle x0 x1 = {s = x0.s ++ x1.s};
    DataTitle = {s = "{{TITLE}}"};
    GoodModifier = {s = "good" | "nice"};
}