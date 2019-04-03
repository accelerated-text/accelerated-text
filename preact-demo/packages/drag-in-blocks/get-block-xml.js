import { h }            from 'preact';
import renderToString   from 'preact-render-to-string';


export default ({ comment = null, fields = {}, type }) =>
    renderToString(
        <xml>
            <block type={ type }>
                { Object.keys( fields ).map( name =>
                    <field key={ name } name={ name }>
                        { fields[name] }
                    </field>
                )}
                { comment &&
                    <comment>{ comment }</comment>
                }
            </block>
        </xml>
    );
