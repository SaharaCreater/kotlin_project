// Experiment Model — pure data class
class Experiment {
  constructor(data) {
    this.id = data.id;
    this.name = data.name;
    this.description = data.description;
    this.category = data.category;
    this.icon = data.icon || 'science';
    this.difficulty = data.difficulty || 'medium';
  }

  get categoryMeta() {
    return Experiment.CATEGORIES[this.category] || Experiment.CATEGORIES.MECHANICS;
  }

  get difficultyLabel() {
    return { easy: 'Начальный', medium: 'Средний', hard: 'Продвинутый' }[this.difficulty] || '';
  }

  static CATEGORIES = {
    MECHANICS:      { name: 'Механика',      color: '#1565C0', bgColor: '#E3F2FD', icon: 'settings' },
    ELECTRICITY:    { name: 'Электричество', color: '#E65100', bgColor: '#FFF3E0', icon: 'bolt' },
    OPTICS:         { name: 'Оптика',         color: '#6A1B9A', bgColor: '#F3E5F5', icon: 'lightbulb' },
    THERMODYNAMICS: { name: 'Термодинамика', color: '#BF360C', bgColor: '#FBE9E7', icon: 'local_fire_department' },
  };
}
