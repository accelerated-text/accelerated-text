import { h, Component } from 'preact';

import useStores        from '../context/use-stores';

import { QA }           from './qa.constants';


export default useStores([
    'planEditor',
])( class UploadDataSample extends Component {

    onClick = () =>
        this.props.planEditor.onClickUpload({
            dataSample: 't-shirts.csv',
        });

    render() {
        const { dataSample } =  this.props.planEditor;

        return (
            <button
                className={ QA.UPLOAD_SAMPLE }
                onClick={ this.onClick }
            >
                { dataSample ? dataSample : 'upload' }
            </button>
        );
    }
});
