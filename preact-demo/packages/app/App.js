import { h } from 'preact';

import AugmentedWriter from '../augmented-writer/AugmentedWriter';

import S from './App.sass';

export default () =>
    <div className={ S.root }>
        <h1>Augmented Writer</h1>
        <AugmentedWriter />
    </div>;
