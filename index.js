import { DeviceEventEmitter, NativeModules, Platform } from 'react-native';

const { DetectSoftNav } = NativeModules;

let softNavDidShow = null;
let softNavDidHide = null;

const addListeners = ({ onShown, onHidden }) => {
          if (Platform.OS === 'ios') return;

          DetectSoftNav.init();
          if (onShown) {
              softNavDidShow = DeviceEventEmitter.addListener('softNavDidShow', onShown);
          }
          if (onHidden) {
              softNavDidHide = DeviceEventEmitter.addListener('softNavDidHide', onHidden);
          }
      },
      isVisible = () => Platform.OS === 'ios' ? Promise.resolve(false) : DetectSoftNav.isVisible();

const removeListener = () => {
    if (Platform.OS === 'ios') return;

    if (softNavDidShow) {
        softNavDidShow.remove();
    }

    if (softNavDidHide) {
        softNavDidHide.remove();
    }
};

const hasSoftKeys       = Platform.OS === 'ios' ? false : DetectSoftNav.hasSoftKeys;
const hasSoftKeysHeight = Platform.OS === 'ios' ? false : DetectSoftNav.hasSoftKeysHeight;

export default { addListeners, isVisible, removeListener, hasSoftKeys, hasSoftKeysHeight };
