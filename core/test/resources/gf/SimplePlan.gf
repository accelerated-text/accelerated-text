abstract Simpleplan = {
 flags
  startcat = DocumentPlan;
 cat
  Data; DocumentPlan; Segment;
 fun
  DocumentPlan01 : Segment -> DocumentPlan;
  Segment02 : Instance -> Segment;
  Data03 : Data;
}