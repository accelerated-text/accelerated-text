import { h } from 'preact';

import WriterExamples       from '../writer-examples/WriterExamples';
import WriterInput          from '../writer-input/WriterInput';
import WriterSegmentEditor  from '../writer-segment-editor/WriterSegmentEditor';

import S from './AugmentedWriter.sass';

export default () =>
    <div className={ S.root }>
        <WriterInput />
        <WriterSegmentEditor />
        <WriterExamples />
        {/* <WriterProofreader /> */}
    </div>;
