import { h }                from 'preact';

import S                    from './Publisher.sass';


export default ({ descriptionText, record }) =>
    <div className={ S.className }>
        { record && [
            <img className={ S.thumbnail } src={ record.thumbnail } />,
            <h3 children={ record.title } className={ S.title } />,
            <p>ISBN: { record['isbn-13'] }</p>,
            <p>{ descriptionText }</p>,
            <button children="Create product" />,
        ]}
    </div>;
