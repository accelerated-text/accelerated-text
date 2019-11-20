concrete GoodBookEng of GoodBook = {
  lincat
    Sentence = {s : Str};
    Data = {s : Str};
    Modifier = {s : Str};
  lin
    GoodTitle m d = {s = m.s ++ d.s};
    DataTitle = {s = "{{TITLE}}"};
    GoodModifier = {s = "good" | "nice"};
}