import { h } from 'preact';

import AugmentedWriter from '../augmented-writer/AugmentedWriter';

import S from './App.sass';

export default () =>
    <div className={ S.className }>
        <h1>Augmented Writer</h1>
        <AugmentedWriter />
    </div>;
