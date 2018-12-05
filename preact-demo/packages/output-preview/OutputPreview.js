import { h } from 'preact';

import S from './OutputPreview.sass';

export default ({ xml }) =>

    <div className={ S.className }>
        { xml
            ? (
                <div>
                    <h2 className={ S.title }>Generated examples:</h2>
                    <div className={ S.example }>{ xml }</div>
                </div>
            )
            : <div>No examples yet.</div>
        }
    </div>;
